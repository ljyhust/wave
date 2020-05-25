package com.wave.user.service;

import com.wave.exception.WaveException;
import com.wave.user.dto.UserInfoDto;
import com.wave.user.dto.req.UserInfoRegisteReqDto;
import com.wave.user.dto.req.UserInfoUpdateReqDto;

public interface UserInfoService {

    void registerUser(UserInfoRegisteReqDto registryReqDto) throws WaveException;

    UserInfoDto getUserInfo(String userId) throws WaveException;
}
