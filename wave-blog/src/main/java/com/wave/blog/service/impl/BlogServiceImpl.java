package com.wave.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wave.blog.config.WaveBlogConstants;
import com.wave.blog.dao.BlogDao;
import com.wave.blog.dao.entity.BlogEntity;
import com.wave.blog.dto.BlogDto;
import com.wave.blog.dto.MessageBlogMqDto;
import com.wave.blog.dto.req.BlogChangeRequest;
import com.wave.blog.rpc.WaveUserFeignClient;
import com.wave.blog.service.BlogService;
import com.wave.common.PageVo;
import com.wave.common.PublicResponseObjDto;
import com.wave.common.TransactionMQClientResult;
import com.wave.common.WaveConstants;
import com.wave.consistency.id.IdGeneratorManager;
import com.wave.exception.WaveException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.redisson.api.RBucket;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class BlogServiceImpl implements BlogService {
    
    private final String FANCY_BLOG_REDIS_PREFIX = "FANCY_BLOG:";
    
    private final String BLOG_ID_REDIS_PREFIX = "BLOG_ID:";
    
    @Autowired
    BlogDao blogDao;
    @Autowired
    @Qualifier("blogMQProducer")
    TransactionMQProducer blogMQProducer;
    @Autowired
    WaveUserFeignClient userFeignClient;
    @Autowired
    RedissonClient redisson;
    
    @Override
    public PageVo<BlogDto> queryMyBlog(Integer pageIndex, Integer pageSize, Long userId) throws WaveException {
        QueryWrapper<BlogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("status", WaveConstants.NORMAL_STATUS);
        Page<Object> page = PageHelper.startPage(pageIndex, pageSize);
        List<BlogEntity> list = blogDao.selectList(queryWrapper);
        page.close();
        PageVo<BlogDto> blogDtoPageVo = new PageVo<>();
        List<BlogDto> resDto = new ArrayList<>();
        list.forEach(e -> {
            BlogDto blogDto = new BlogDto();
            blogDto.setId(e.getId());
            blogDto.setContent(e.getContent());
            resDto.add(blogDto);
        });
        blogDtoPageVo.setRows(resDto);
        blogDtoPageVo.setPageIndex(pageIndex);
        blogDtoPageVo.setPageSize(pageSize);
        blogDtoPageVo.setPageCount(page.getPages());
        blogDtoPageVo.setTotal((int)page.getTotal());
        return blogDtoPageVo;
    }
    
    @Override
    public PageVo<BlogDto> queryFocusBlog(Integer pageIndex, Integer pageSize, Long userId) throws WaveException {
        // query redis queue
        RScoredSortedSet<Long> sortedSet = redisson.getScoredSortedSet(FANCY_BLOG_REDIS_PREFIX + userId, LongCodec.INSTANCE);
        int start = (pageIndex - 1) * pageSize;
        int end = pageIndex * pageSize - 1;
        int total = sortedSet.size();
        Collection<Long> blogIds = sortedSet.valueRange(start, end);
        // query blog from db
        List<BlogEntity> blogEntities = blogDao.selectBatchIds(blogIds);
        List<BlogDto> vos = new ArrayList<>();
        blogEntities.forEach(e -> {
            BlogDto blogDto = new BlogDto();
            blogDto.setContent(e.getContent());
            blogDto.setId(e.getId());
            blogDto.setUserId(e.getUserId());
            vos.add(blogDto);
        });
        PageVo<BlogDto> resPage = new PageVo<>();
        resPage.setTotal(total);
        resPage.setPageCount(total / pageSize + 1);
        resPage.setPageIndex(pageIndex);
        resPage.setPageSize(pageSize);
        resPage.setRows(vos);
        return resPage;
    }
    
    @Override
    public BlogEntity queryBlogById(Long id) throws WaveException {
        RBucket<BlogEntity> bucket = redisson.getBucket(BLOG_ID_REDIS_PREFIX + id);
        BlogEntity blogEntity = bucket.get();
        // 存在，但为空值
        if (null != blogEntity && null == blogEntity.getId()) {
            return null;
        }
        // query mysql db
        BlogEntity blog = blogDao.selectById(id);
        if (null == blog) {
            blog = new BlogEntity();
        }
        bucket.setAsync(blog, RandomUtils.nextInt(60, 120), TimeUnit.MINUTES);
        return blog;
    }
    
    @Override
    public void blogEdit(BlogChangeRequest request, Long userId) throws WaveException {
        //根据id 查帖子
        BlogEntity blogEntity = queryBlogById(request.getId());
        //判断是否同一作者
        if (null == blogEntity || !userId.equals(blogEntity.getUserId())) {
            throw new WaveException(WaveException.INVALID_PARAM, "invalid blog params");
        }
        BlogEntity updateBlog = new BlogEntity();
        updateBlog.setId(request.getId());
        updateBlog.setUserId(userId);
        updateBlog.setContent(request.getContent());
        blogDao.updateById(updateBlog);
    }
    
    @Override
    public TransactionMQClientResult blogPublish(BlogChangeRequest request, Long userId) throws WaveException {
        // generator id
        long id = IdGeneratorManager.getGenerator(WaveBlogConstants.WAVE_BLOG_NAME).nextId(userId);
        MessageBlogMqDto messageBlogMqDto = new MessageBlogMqDto();
        messageBlogMqDto.setBlogId(id);
        messageBlogMqDto.setContent(request.getContent());
        messageBlogMqDto.setCreateTs(new Date());
        messageBlogMqDto.setUserId(userId);
        messageBlogMqDto.setOperationType(WaveBlogConstants.WAVE_BLOG_MSG_TYPE_NEW);
        // mq notify blog change with half transaction
        try {
            Message message = new Message(WaveBlogConstants.WAVE_BLOG_MQ_TOPIC, WaveBlogConstants.MQ_TAG_WAVE_BLOG_MSG_NEW,
                    JSON.toJSONString(messageBlogMqDto).getBytes(RemotingHelper.DEFAULT_CHARSET));
            TransactionSendResult result = blogMQProducer.sendMessageInTransaction(message, WaveBlogConstants.WAVE_BLOG_MSG_TYPE_NEW);
            log.info("=====> transaction blog mq {}  result {}", messageBlogMqDto, result);
            if (result.getLocalTransactionState() == LocalTransactionState.ROLLBACK_MESSAGE) {
                throw new WaveException(WaveException.SERVER_ERROR, "发送失败");
            }
            if (result.getLocalTransactionState() == LocalTransactionState.UNKNOW) {
                TransactionMQClientResult transactionResult = new TransactionMQClientResult();
                transactionResult.setTransactionCode(1);
                transactionResult.setTimeout(60000);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("blogId", messageBlogMqDto.getBlogId());
                transactionResult.setData(jsonObject);
                return transactionResult;
            }
            
            SendStatus sendStatus = result.getSendStatus();
            if (sendStatus != SendStatus.SEND_OK) {
                throw new WaveException(WaveException.OVER_THRESHOLD, "server is busy");
            }
            
        } catch (UnsupportedEncodingException | MQClientException e) {
            e.printStackTrace();
            throw new WaveException(WaveException.OVER_THRESHOLD, "server is busy");
        }
        TransactionMQClientResult result = new TransactionMQClientResult();
        messageBlogMqDto.setContent(null);
        result.setData(messageBlogMqDto);
        return result;
    }
    
    @Override
    public void blogDelete(Long blogId, Long userId) throws WaveException {
        // validate userId & blogId
        BlogEntity blogEntity = blogDao.selectById(blogId);
        if (null == blogEntity || !userId.equals(blogEntity.getUserId())
                || WaveConstants.NORMAL_STATUS != (short)blogEntity.getStatus()) {
            log.info("====> blog {} not exist");
            throw new WaveException(WaveException.INVALID_PARAM, "invalid param, no blog query");
        }
        MessageBlogMqDto messageBlogMqDto = new MessageBlogMqDto();
        messageBlogMqDto.setBlogId(blogId);
        messageBlogMqDto.setUserId(userId);
        messageBlogMqDto.setCreateTs(blogEntity.getCreateTs());
        messageBlogMqDto.setOperationType(WaveBlogConstants.WAVE_BLOG_MSG_TYPE_DELELTE);
    
        try {
            Message message = new Message(WaveBlogConstants.WAVE_BLOG_MQ_TOPIC, WaveBlogConstants.MQ_TAG_WAVE_BLOG_MSG_DELETE,
                    JSON.toJSONString(messageBlogMqDto).getBytes(RemotingHelper.DEFAULT_CHARSET));
            TransactionSendResult result = blogMQProducer.sendMessageInTransaction(message, WaveBlogConstants.WAVE_BLOG_MSG_TYPE_DELELTE);
            log.info("=====> transaction blog mq {}  result {}", messageBlogMqDto, result);
            if (result.getLocalTransactionState() == LocalTransactionState.ROLLBACK_MESSAGE) {
                throw new WaveException(WaveException.SERVER_ERROR, "发送失败");
            }
            if (result.getLocalTransactionState() == LocalTransactionState.UNKNOW) {
                throw new WaveException(201, "发送中...");
            }
    
            SendStatus sendStatus = result.getSendStatus();
            if (sendStatus != SendStatus.SEND_OK) {
                throw new WaveException(WaveException.OVER_THRESHOLD, "server is busy");
            }
        } catch (UnsupportedEncodingException | MQClientException e) {
            e.printStackTrace();
            throw new WaveException(WaveException.OVER_THRESHOLD, "server is busy");
        }
    }
    
    @Override
    public boolean blogMQConsumerHandler(MessageBlogMqDto messageBlogMqDto) throws WaveException {
        Long userId = messageBlogMqDto.getUserId();
        Date createTs = messageBlogMqDto.getCreateTs();
        // rpc wave-user fansUserIdList
        PublicResponseObjDto dto = userFeignClient.myFancyUserIdList(userId);
        if (dto.getCode() != 200) {
            log.info("====> feign request error {}", messageBlogMqDto);
            return false;
        }
        // redis zset fans_blog:userId blogId createTs
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(dto.getData()));
        JSONArray fancyUserIdList = jsonObject.getJSONArray("fancyUserIdList");
        if (CollectionUtils.isEmpty(fancyUserIdList)) {
            return true;
        }
        long score = 0 - createTs.getTime();
        switch (messageBlogMqDto.getOperationType()) {
            case WaveBlogConstants.WAVE_BLOG_MSG_TYPE_NEW:
                // TODO 循环是否有优化空间，优化网络，lua脚本
                // zadd key score member
                // eval "redis.call('zadd' keys[1] agv[1] argv[2] ...)" 1 fancy_blog_redis:xxx  132143 id1  3345 id2 ....
                // eval "redis.call('zadd', KEYS[1], ARGV[1], ARGV[2], ARGV[3], ARGV[4])" 1 fancy_blog_redis:test 1 id1  2 id2
                StringBuffer scriptBuffer = new StringBuffer();
                String format = "redis.call('zadd', KEYS[%d], ARGV[1], ARGV[2]);";
                List<Object> keys = new ArrayList<>();
                for(int i = 1; i <= fancyUserIdList.size(); i++) {
                    String script = String.format(format, i);
                    scriptBuffer.append(script);
                    keys.add(FANCY_BLOG_REDIS_PREFIX + fancyUserIdList.get(i - 1).toString());
                }
                scriptBuffer.append("return nil;");
                Object res = null;
                try {
                    res = redisson.getScript(LongCodec.INSTANCE)
                            .eval(RScript.Mode.READ_WRITE, scriptBuffer.toString(), RScript.ReturnType.STATUS, keys,
                                    Long.valueOf(score), messageBlogMqDto.getBlogId());
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                log.info("=====> redis res  {}", res);
                /*for (Object o : fancyUserIdList) {
                    redisson.getScoredSortedSet(WaveBlogConstants.FANCY_BLOG_REDIS_PREFIX + ":" + o.toString())
                            .addAsync(score, messageBlogMqDto.getBlogId());
                }*/
                break;
            case WaveBlogConstants.WAVE_BLOG_MSG_TYPE_DELELTE:
                // eval "redis.call('zrem' keys[1] argv[1] argv[2] ...)" 1 fancy_blog_redis:xxx  id1  id2 ...
                StringBuffer scriptBuffer2 = new StringBuffer();
                String format2 = "redis.call('zrem', KEYS[%d], ARGV[1]);";
                List<Object> keys2 = new ArrayList<>();
                for(int i = 1; i <= fancyUserIdList.size(); i++) {
                    String script = String.format(format2, i);
                    scriptBuffer2.append(script);
                    keys2.add(FANCY_BLOG_REDIS_PREFIX + fancyUserIdList.get(i - 1).toString());
                }
                scriptBuffer2.append("return nil;");
                Object res2 = redisson.getScript(LongCodec.INSTANCE)
                        .eval(RScript.Mode.READ_WRITE, scriptBuffer2.toString(), RScript.ReturnType.STATUS, keys2,
                                messageBlogMqDto.getBlogId());
                log.info("=====> redis res  {}", res2);
                /*for (Object o : fancyUserIdList) {
                    redisson.getScoredSortedSet(WaveBlogConstants.FANCY_BLOG_REDIS_PREFIX + ":" + o.toString())
                            .removeAsync(messageBlogMqDto.getBlogId().toString());
                }*/
                break;
            default:
                break;
        }
        return true;
    }
}
