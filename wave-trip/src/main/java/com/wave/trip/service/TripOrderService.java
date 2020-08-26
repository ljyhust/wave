package com.wave.trip.service;

import com.wave.exception.WaveException;
import com.wave.trip.dto.req.TripDiscardReqDto;
import com.wave.trip.dto.req.TripNewReqDto;
import com.wave.trip.vo.TripOrderVo;

public interface TripOrderService {

    /**
     * 新行程订单
     * @param tripNewReqDto
     * @throws WaveException
     */
    void addTripOrder(TripNewReqDto tripNewReqDto) throws Exception;

    /**
     * 取消订单
     * @param tripDiscardReqDto
     * @throws WaveException
     */
    void discardTripOrder(TripDiscardReqDto tripDiscardReqDto) throws WaveException;

    /**
     * 查询用户当前订单
     * @param account 账号信息
     * @return
     * @throws WaveException
     */
    TripOrderVo userTripOrderCurrent(String account) throws WaveException;
}
