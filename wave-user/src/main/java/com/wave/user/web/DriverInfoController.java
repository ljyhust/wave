package com.wave.user.web;

import com.wave.common.PublicResponseDto;
import com.wave.common.PublicResponseObjDto;
import com.wave.common.PublicResponseUtil;
import com.wave.exception.WaveException;
import com.wave.user.dto.DriverInfoDto;
import com.wave.user.dto.req.DriverInfoUpdateReqDto;
import com.wave.user.service.DriverInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("driver")
public class DriverInfoController {

    @Autowired
    DriverInfoService driverInfoService;

    @PostMapping("updateInfo")
    public PublicResponseDto driverInfoUpdate(@RequestBody DriverInfoUpdateReqDto reqDto) throws WaveException {
        driverInfoService.driverInfoUpdate(reqDto);
        return PublicResponseUtil.publicResponseDto();
    }

    @RequestMapping("info/{account}")
    public PublicResponseObjDto driverInfoByAccount(@PathVariable("account") String account) throws WaveException {
        DriverInfoDto driverInfo = driverInfoService.getDriverInfoByAccount(account);
        return PublicResponseUtil.okPublicResponseObjDto(driverInfo);
    }
}
