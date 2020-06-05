package com.wave.user.service;

import com.wave.exception.WaveException;
import com.wave.user.dto.DriverInfoDto;
import com.wave.user.dto.req.DriverInfoUpdateReqDto;

public interface DriverInfoService {

    /**
     * 更新车主信息
     * @param reqDto
     * @throws WaveException
     */
    void driverInfoUpdate(DriverInfoUpdateReqDto reqDto) throws WaveException;

    /**
     * 查询车主信息
     * @param account 账号
     * @return
     * @throws WaveException
     */
    public DriverInfoDto getDriverInfoByAccount(String account) throws WaveException;
}
