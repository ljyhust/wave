package com.wave.trip.api;

import com.alibaba.fastjson.JSON;
import com.wave.common.PublicResponseObjDto;
import com.wave.trip.WaveTripApplicationTests;
import com.wave.trip.rpc.WaveUserFeign;
import com.wave.user.api.dto.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class WaveUserFeignTest extends WaveTripApplicationTests{

    @Autowired
    WaveUserFeign waveUserFeign;

    @Test
    public void testGetUserInfo() throws Exception {
        PublicResponseObjDto<UserInfoDto> account = waveUserFeign.getUserInfoByAccount("lijinyang");
        log.info("====> response {} ", JSON.toJSONString(account));
    }
}
