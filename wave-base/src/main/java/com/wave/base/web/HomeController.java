package com.wave.base.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    public Object index() {
        return "{\"key\":\"test\"}";
    }
}
