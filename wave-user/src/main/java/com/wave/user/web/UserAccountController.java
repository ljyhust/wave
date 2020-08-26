package com.wave.user.web;

import com.wave.common.PublicResponseDto;
import com.wave.common.PublicResponseUtil;
import com.wave.exception.WaveException;
import com.wave.user.dto.req.AccountRegistryReqDto;
import com.wave.user.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("account")
public class UserAccountController {

    @Autowired
    UserAccountService userAccountService;
    @PostMapping("registry")
    public PublicResponseDto registryUser(@Validated AccountRegistryReqDto reqDto) throws WaveException {
        userAccountService.registerUserAccount(reqDto);
        return PublicResponseUtil.publicResponseDto();
    }
}
