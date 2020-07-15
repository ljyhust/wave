package com.wave.trip.web;

import com.wave.common.PublicResponseDto;
import com.wave.common.PublicResponseUtil;
import com.wave.trip.dto.req.TripDiscardReqDto;
import com.wave.trip.dto.req.TripNewReqDto;
import com.wave.trip.service.TripOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@Slf4j
@RestController
@RequestMapping("user")
public class UserTripOrderController {

    @Autowired
    TripOrderService tripOrderService;

    /**
     * 用户发起新行程订单
     * @param tripNewReqDto 行程dto
     * @return
     * @throws Exception
     */
    @PostMapping("addTripOrder")
    public PublicResponseDto addTripOrderUser(@Validated @RequestBody TripNewReqDto tripNewReqDto) throws Exception{
        tripOrderService.addTripOrder(tripNewReqDto);
        return PublicResponseUtil.publicResponseDto();
    }

    @PostMapping("discardOrder")
    public PublicResponseDto discardTripOrderUser(@Validated @RequestBody TripDiscardReqDto tripDiscardReqDto) throws Exception {
        tripOrderService.discardTripOrder(tripDiscardReqDto);
        return PublicResponseUtil.publicResponseDto();
    }

    /**
     * 查询是否有订单未完成，如果有返回orderId，没有则返回空
     * @return
     * @throws Exception
     */
    @PostMapping("myTripOrder/{account}")
    public PublicResponseDto userTripOrderStatus(@PathVariable("account") @NotBlank String account) throws Exception {
        return null;
    }
}
