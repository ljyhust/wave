package com.wave.user.service;

import com.wave.exception.WaveException;
import com.wave.user.dto.req.AccountRegistryReqDto;
import com.wave.user.dto.vo.UserIdVo;

public interface UserAccountService {

    UserIdVo registerUserAccount(AccountRegistryReqDto reqDto) throws WaveException;
}
