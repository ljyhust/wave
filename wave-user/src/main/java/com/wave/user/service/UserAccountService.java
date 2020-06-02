package com.wave.user.service;

import com.wave.exception.WaveException;
import com.wave.user.dto.req.AccountRegistryReqDto;

public interface UserAccountService {

    void registerUserAccount(AccountRegistryReqDto reqDto) throws WaveException;
}
