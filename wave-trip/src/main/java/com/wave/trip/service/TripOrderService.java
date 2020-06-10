package com.wave.trip.service;

import com.wave.exception.WaveException;
import com.wave.trip.dto.req.TripNewReqDto;

public interface TripOrderService {

    /**
     * 新行程订单
     * @param tripNewReqDto
     * @throws WaveException
     */
    void addTripOrder(TripNewReqDto tripNewReqDto) throws WaveException;
}
