package com.wave.trip.service.impl;

import com.wave.exception.WaveException;
import com.wave.trip.dao.TripOrderDao;
import com.wave.trip.dto.req.TripNewReqDto;
import com.wave.trip.service.TripOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TripOrderServiceImpl implements TripOrderService{

    @Autowired
    TripOrderDao tripOrderDao;

    @Override
    public void addTripOrder(TripNewReqDto tripNewReqDto) throws WaveException {

    }
}
