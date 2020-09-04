package com.wave.demo;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class HttpConnectTests {

    @Test
    public void testGetConnect() {
        String forObject = new RestTemplate().getForObject("http://baidu.com", String.class);
        System.out.println(forObject);
    }
}
