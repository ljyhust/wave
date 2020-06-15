package com.wave.trip.service.impl;

import com.alibaba.csp.sentinel.SphO;
import com.alibaba.fastjson.JSON;
import com.wave.common.PublicResponseObjDto;
import com.wave.exception.WaveException;
import com.wave.trip.config.WaveTripConstants;
import com.wave.trip.dao.TripOrderDao;
import com.wave.trip.dao.entity.TripOrderEntity;
import com.wave.trip.dto.req.TripNewReqDto;
import com.wave.trip.rpc.WaveUserFeign;
import com.wave.trip.service.TripOrderService;
import com.wave.trip.service.TripOrderState;
import com.wave.user.api.dto.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SunlandsProducer;
import org.apache.rocketmq.common.message.Message;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TripOrderServiceImpl implements TripOrderService{

    @Autowired
    TripOrderDao tripOrderDao;

    @Autowired
    @Qualifier("tripOrderProducer")
    SunlandsProducer producer;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    WaveUserFeign waveUserFeign;

    @Override
    public void addTripOrder(TripNewReqDto tripNewReqDto) throws WaveException {
        // 获取用户userId
        PublicResponseObjDto<UserInfoDto> accountInfo = waveUserFeign.getUserInfoByAccount(tripNewReqDto.getAccount());
        TripOrderEntity tripOrderEntity = new TripOrderEntity();
        BeanUtils.copyProperties(tripNewReqDto, tripOrderEntity);
        tripOrderEntity.setUserId(accountInfo.getData().getUserId());
        tripOrderDao.insert(tripOrderEntity);
        boolean resFlag = true;
        // mq发送新订单，如果发送失败怎么处理？回滚？
        // 异步发送，实现异步回调通知
        // 两种情况分析：1. 服务挂了  2. 网络错误，即远程mq无法访问
        // 1. 本地服务挂了，则未发送MQ，客户端无法收到正常response，而订单却正常？订单状态默认为0，此时初始化，但用户不可见。用户可见匹配中的，取消的、成功的订单
        // 2. 网络错误，回调方法SendCallback处理？如果本地不知道是否发送成功，或者MQ broker在回调时发生闪断，怎么处理？(消费方保证幂等性)  mq client主动询问？
        //    如果有要求，可以采用同步发送机制；异步发送，通过回调，发送失败反馈补偿，消费方需要保证消息幂等。
        Message message = new Message(producer.getTopic(), "tripOrderNew", tripOrderEntity.getId().toString(),
                JSON.toJSONBytes(tripOrderEntity));
        try {
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult, Message message) {
                    // 发送成功，修改订单状态为 匹配ing
                    TripOrderEntity updateEntity = new TripOrderEntity();
                    updateEntity.setTripState(TripOrderState.DISPATCH_ING.getState());
                    updateEntity.setId(tripOrderEntity.getId());
                    tripOrderDao.updateById(updateEntity);
                    // FIXME 更新用户当前状态（redis）为行程匹配中？
                    redissonClient.getBucket(WaveTripConstants.USER_TRIP_STATE_KEY + tripNewReqDto.getAccount());
                }

                @Override
                public void onException(Throwable e) {
                    // 由 client 轮询查询当前是否有行程
                    log.error("====> 发送MQ tripOrder 失败 {}", e);
                }
            });
        } catch (Exception e) {
            log.error("====> 发送MQ tripOrder 失败 {}", e);
            throw new WaveException(WaveException.SERVER_ERROR, "行程创建失败");
        }
        // FIXME 匹配过程中，取消订单如何处理？
    }
}
