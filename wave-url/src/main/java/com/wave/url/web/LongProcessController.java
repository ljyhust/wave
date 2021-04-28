package com.wave.url.web;

import com.wave.common.PublicResponseDto;
import com.wave.common.PublicResponseUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("shutdownTest")
public class LongProcessController {
    
    @GetMapping("testShutdown")
    public PublicResponseDto longProcessTest() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return PublicResponseUtil.publicResponseDto();
    }
}
