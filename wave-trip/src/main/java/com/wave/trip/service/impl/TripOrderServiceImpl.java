package com.wave.trip.service.impl;

import com.alibaba.fastjson.JSON;
import com.wave.exception.WaveException;
import com.wave.trip.dao.TripOrderDao;
import com.wave.trip.dao.entity.TripOrderEntity;
import com.wave.trip.dto.req.TripNewReqDto;
import com.wave.trip.service.TripOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SunlandsProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
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

    @Override
    public void addTripOrder(TripNewReqDto tripNewReqDto) throws WaveException {
        TripOrderEntity tripOrderEntity = new TripOrderEntity();
        BeanUtils.copyProperties(tripNewReqDto, tripOrderEntity);
        tripOrderDao.insert(tripOrderEntity);

        // mq发送新订单，如果发送失败怎么处理？回滚？
        // 异步发送，实现异步回调通知
        // 两种情况分析：1. 服务挂了  2. 网络错误，即远程mq无法访问
        // 1. 本地服务挂了，则未发送MQ，客户端无法收到正常response，而订单却正常？订单状态默认为0，此时初始化，但用户不可见。用户可见匹配中的，取消的、成功的订单
        // 2. 网络错误，回调方法SendCallback处理？如果本地不知道是否发送成功，或者MQ broker在回调时发生闪断，怎么处理？mq client主动询问？
        Message message = new Message(producer.getTopic(), "tripOrderNew", tripOrderEntity.getId().toString(),
                JSON.toJSONBytes(tripOrderEntity));
        try {
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult, Message message) {

                }

                @Override
                public void onException(Throwable throwable) {
                    // 补偿消息？或者告诉app客户端用户失败
                }
            });
        } catch (Exception e) {
            log.error("====> 发送消息失败  {}", e);
        }
        // 匹配过程中，取消订单如何处理？
    }
}
