package com.wave.blog.rpc;

import com.wave.common.PageQueryRequestDto;
import com.wave.common.PublicResponseObjDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="wave-user", path = "wave-user/user")
public interface WaveUserFeignClient {
    
    @PostMapping("myFancyUserIds/{userId}")
    public PublicResponseObjDto myFancyUserIdList(@PathVariable("userId") Long userId);
}
