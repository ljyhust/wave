package com.wave.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wave.common.PageVo;
import com.wave.common.WaveConstants;
import com.wave.exception.WaveException;
import com.wave.user.api.dto.UserInfoDto;
import com.wave.user.dao.UserConcernDao;
import com.wave.user.dao.UserFancyDao;
import com.wave.user.dao.UserInfoDao;
import com.wave.user.dao.entity.UserConcernEntity;
import com.wave.user.dao.entity.UserFancyEntity;
import com.wave.user.dao.entity.UserInfoEntity;
import com.wave.user.dto.vo.MyConcernUserVo;
import com.wave.user.dto.vo.MyFancyUserVo;
import com.wave.user.service.FriendRelationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FriendRelationServiceImpl implements FriendRelationService{

    private final String MY_CONCERN_USER_INFO_PREFIX = "CONCERN_USER_INFOS:";

    private final String MY_FANCY_USER_INFO_PREFIX = "FANCY_USER_INFOS:";

    private final String MY_CONCERN_USER_WRITE_READ_LOCK_PREFIX = "CONCERN_USER_WRITE_READ_LOCK:";

    @Autowired
    UserConcernDao userConcernDao;

    @Autowired
    UserFancyDao userFancyDao;

    @Autowired
    UserInfoDao userInfoDao;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public MyConcernUserVo queryMyConcernUserInfo(Long userId) throws WaveException {
        RBucket<MyConcernUserVo> bucket = redissonClient.getBucket(MY_CONCERN_USER_INFO_PREFIX + userId);
        MyConcernUserVo value = bucket.get();
        if (null != value) {
            return value;
        }

        // res vo
        MyConcernUserVo myConcernUserVo = new MyConcernUserVo();
        readLockCloudRun(MY_CONCERN_USER_WRITE_READ_LOCK_PREFIX + userId, new Runnable() {
            @Override
            public void run() {
                List<UserInfoDto> userInfos = getFriendUserInfos(userId, "concernUserId", userConcernDao);
                myConcernUserVo.setUserId(userId);
                myConcernUserVo.setConcernUserList(userInfos);
            }
        });
        bucket.setAsync(myConcernUserVo);
        return myConcernUserVo;
    }

    @Override
    public MyFancyUserVo queryMyFancyUserInfo(Long userId) throws WaveException {
        RBucket<MyFancyUserVo> bucket = redissonClient.getBucket(MY_FANCY_USER_INFO_PREFIX + userId);
        MyFancyUserVo value = bucket.get();
        if (null != value) {
            return value;
        }
        // res vo
        MyFancyUserVo myFancyUserVo = new MyFancyUserVo();
        List<UserInfoDto> userInfos = getFriendUserInfos(userId, "fancyUserId", userFancyDao);
        myFancyUserVo.setUserId(userId);
        myFancyUserVo.setFancyUserList(userInfos);
        bucket.setAsync(myFancyUserVo, RandomUtils.nextInt(10, 100), TimeUnit.SECONDS);
        return myFancyUserVo;
    }

    @Override
    public void focusUser(Long userId, Long focusUserId) throws WaveException {

        UserConcernEntity userConcernEntity = new UserConcernEntity();
        userConcernEntity.setUserId(userId);
        userConcernEntity.setConcernUserId(focusUserId);

        UserFancyEntity userFancyEntity = new UserFancyEntity();
        userFancyEntity.setUserId(focusUserId);
        userFancyEntity.setFancyUserId(userId);

        // 删除缓存
        // 加读写锁，防止点关注时多次点击或提交，或防止读写不一致
        writeLockCloudRun(MY_CONCERN_USER_WRITE_READ_LOCK_PREFIX + userId, new Runnable() {
            @Override
            public void run() {
                redissonClient.getBucket(MY_FANCY_USER_INFO_PREFIX + focusUserId).delete();
                redissonClient.getBucket(MY_CONCERN_USER_INFO_PREFIX + userId).delete();
                userConcernDao.insert(userConcernEntity);
                userFancyDao.insert(userFancyEntity);
            }
        });
    }

    @Override
    public void unFocusUser(Long userId, Long focusUserId) throws WaveException {
        UserConcernEntity userConcernEntity = new UserConcernEntity();
        userConcernEntity.setStatus(WaveConstants.DEL_STATUS);
        UpdateWrapper<UserConcernEntity> userConcernEntityUpdateWrapper = new UpdateWrapper<>();
        userConcernEntityUpdateWrapper.eq("user_id", userId);
        userConcernEntityUpdateWrapper.eq("concern_user_id", focusUserId);

        UserFancyEntity userFancyEntity = new UserFancyEntity();
        userFancyEntity.setStatus(WaveConstants.DEL_STATUS);
        UpdateWrapper<UserFancyEntity> userFancyEntityUpdateWrapper = new UpdateWrapper<>();
        userFancyEntityUpdateWrapper.eq("user_id", focusUserId);
        userFancyEntityUpdateWrapper.eq("fancy_user_id", userId);

        // 删除缓存
        writeLockCloudRun(MY_CONCERN_USER_WRITE_READ_LOCK_PREFIX + userId, new Runnable() {
            @Override
            public void run() {
                redissonClient.getBucket(MY_FANCY_USER_INFO_PREFIX + focusUserId).delete();
                redissonClient.getBucket(MY_CONCERN_USER_INFO_PREFIX + userId).delete();
                userConcernDao.update(userConcernEntity, userConcernEntityUpdateWrapper);
                userFancyDao.update(userFancyEntity, userFancyEntityUpdateWrapper);
            }
        });
    }
    
    @Override
    public PageVo myConcernedUsers(Long userId, Integer pageIndex, Integer pageSize) throws WaveException {
        // 查询我的关注用户
        MyConcernUserVo myConcernUserVo = queryMyConcernUserInfo(userId);
        List<UserInfoDto> concernUserList = myConcernUserVo.getConcernUserList();
        return pageFriendUsers(concernUserList, pageIndex, pageSize);
    }
    
    @Override
    public PageVo myFancyUsers(Long userId, Integer pageIndex, Integer pageSize) throws WaveException {
        MyFancyUserVo myFancyUserVo = queryMyFancyUserInfo(userId);
        List<UserInfoDto> userList = myFancyUserVo.getFancyUserList();
        return pageFriendUsers(userList, pageIndex, pageSize);
    }
    
    @Override
    public List<Long> myFancyUserIds(Long userId) throws WaveException {
        QueryWrapper<UserFancyEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("status", WaveConstants.NORMAL_STATUS);
        List<UserFancyEntity> list = userFancyDao.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().map(UserFancyEntity::getFancyUserId).collect(Collectors.toList());
    }
    
    private PageVo pageFriendUsers(List<UserInfoDto> userList, Integer pageIndex, Integer pageSize) throws WaveException {
        PageVo<UserInfoDto> userInfoDtoPageVo = new PageVo<>();
        userInfoDtoPageVo.setPageIndex(pageIndex);
        userInfoDtoPageVo.setPageSize(pageSize);
        if (CollectionUtils.isEmpty(userList)) {
            return userInfoDtoPageVo;
        }
        // 查询用户信息
        List<UserInfoDto> res = userList.stream().skip(pageIndex * pageSize).limit(pageSize)
                .collect(Collectors.toList());
        userInfoDtoPageVo.setPageCount(userList.size() / pageSize);
        userInfoDtoPageVo.setRows(res);
        return userInfoDtoPageVo;
    }
    
    private <T> List<UserInfoDto> getFriendUserInfos(Long userId, String idName, BaseMapper<T> baseMapper) {
        // 获取concernUserIds
        QueryWrapper<T> userConcernEntityQueryWrapper = new QueryWrapper<>();
        userConcernEntityQueryWrapper.eq("user_id", userId);
        userConcernEntityQueryWrapper.eq("status", WaveConstants.NORMAL_STATUS);
        List<T> userConcernEntities = baseMapper.selectList(userConcernEntityQueryWrapper);

        // 批量查询用户信息
        Set<Long> concernUserIds = userConcernEntities.stream().map(e -> {
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(e));
            return jsonObject.getLong(idName);
        }).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(concernUserIds)) {
            return null;
        }

        // 批量查询的分库分表是怎么做的？sql语句在所有条件的hash库中都执行一次，然后聚合结果
        List<UserInfoEntity> userInfoEntities = userInfoDao.selectBatchIds(concernUserIds);

        List<UserInfoDto> concernUserInfos = new ArrayList<>();

        userInfoEntities.forEach(e -> {
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setUserId(e.getId());
            userInfoDto.setImageUrl(e.getImageUrl());
            userInfoDto.setNickName(e.getNickName());
            //userInfoDto.setMobile(e.getMobile());
            //userInfoDto.setEmail(e.getEmail());
            concernUserInfos.add(userInfoDto);
        });
        return concernUserInfos;
    }

    private void writeLockCloudRun(String key, Runnable task) {
        RLock lock = redissonClient.getReadWriteLock(key).writeLock();
        try {
            boolean ifLocked = lock.tryLock(1000, 1000, TimeUnit.MILLISECONDS);
            if (!ifLocked) {
                throw new WaveException(WaveException.SERVER_ERROR, "服务忙，请稍候再尝试");
            }
            task.run();
        } catch (Exception e) {
            log.error("=====> focus user error {}", e);
        } finally {
            if (null != lock) {
                lock.forceUnlockAsync();
            }
        }
    }

    private void readLockCloudRun(String key, Runnable task) {
        RLock lock = redissonClient.getReadWriteLock(key).readLock();
        try {
            boolean ifLocked = lock.tryLock(1000, 1000, TimeUnit.MILLISECONDS);
            if (!ifLocked) {
                throw new WaveException(WaveException.SERVER_ERROR, "服务忙，请稍候再尝试");
            }
            task.run();
        } catch (Exception e) {
            log.error("=====> focus user error {}", e);
        } finally {
            if (null != lock) {
                lock.forceUnlockAsync();
            }
        }
    }
}
