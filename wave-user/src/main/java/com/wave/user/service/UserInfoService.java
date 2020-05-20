package com.wave.user.service;

import com.wave.exception.WaveException;
import com.wave.user.dao.UserInfoDao;
import com.wave.user.dto.req.UserRegistryReqDto;
import org.springframework.beans.factory.annotation.Autowired;

public interface UserInfoService {

    void registerUser(UserRegistryReqDto registryReqDto) throws WaveException;
}
