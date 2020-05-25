package com.wave.user.web;

import com.wave.common.PublicResponseDto;
import com.wave.common.PublicResponseObjDto;
import com.wave.common.PublicResponseUtil;
import com.wave.user.dto.UserInfoDto;
import com.wave.user.dto.req.UserInfoRegisteReqDto;
import com.wave.user.dto.req.UserInfoUpdateReqDto;
import com.wave.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserInfoController {

    @Autowired
    UserInfoService userInfoService;

    @PostMapping("updateUser")
    public PublicResponseDto updateUserInfo(@Validated UserInfoUpdateReqDto reqDto) throws Exception {
        //userInfoService.registerUser();
        return new PublicResponseDto();
    }

    @PostMapping("register")
    public PublicResponseDto registerUserInfo(@Validated UserInfoRegisteReqDto reqDto) throws Exception {
        userInfoService.registerUser(reqDto);
        return PublicResponseUtil.publicResponseDto();
    }

    @PostMapping("info/{userId}")
    public PublicResponseObjDto getUserInfo(@PathVariable("userId") String userId) throws Exception {

        UserInfoDto userInfo = userInfoService.getUserInfo(userId);
        return PublicResponseUtil.okPublicResponseObjDto(userInfo);
    }
}
