package com.wave.trip.rpc;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.wave.common.PublicResponseObjDto;
import com.wave.user.api.dto.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="wave-user", path = "wave-user/user")
public interface WaveUserFeign {

    @PostMapping("info/account/{account}")
    PublicResponseObjDto<UserInfoDto> getUserInfoByAccount(@PathVariable("account") String account);
}
