package com.wave.user.service;

import com.wave.exception.WaveException;
import com.wave.user.dto.UserInfoDto;
import com.wave.user.dto.req.UserInfoModifyReqDto;
import com.wave.user.dto.req.UserInfoUpdateReqDto;

public interface UserInfoService {

    void registerUser(UserInfoModifyReqDto registryReqDto) throws WaveException;

    UserInfoDto getUserInfo(String userId) throws WaveException;

    UserInfoDto getUserInfoByAccount(String account) throws WaveException;

    void userInfoUpdate(UserInfoUpdateReqDto reqDto) throws WaveException;
}
