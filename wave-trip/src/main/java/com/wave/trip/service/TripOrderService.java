package com.wave.trip.service;

import com.wave.exception.WaveException;
import com.wave.trip.dto.req.TripDiscardReqDto;
import com.wave.trip.dto.req.TripNewReqDto;

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
}
