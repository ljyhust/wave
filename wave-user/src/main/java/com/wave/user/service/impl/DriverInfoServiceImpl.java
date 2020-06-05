package com.wave.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wave.exception.WaveException;
import com.wave.user.dao.DriverInfoDao;
import com.wave.user.dao.UserAccountDao;
import com.wave.user.dao.entity.AccountEntity;
import com.wave.user.dao.entity.DriverInfoEntity;
import com.wave.user.dto.DriverInfoDto;
import com.wave.user.dto.req.DriverInfoUpdateReqDto;
import com.wave.user.service.DriverInfoService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DriverInfoServiceImpl implements DriverInfoService{

    @Autowired
    DriverInfoDao driverInfoDao;
    @Autowired
    UserAccountDao accountDao;

    @Autowired
    RedissonClient redissonClient;

    private final String DRIVER_INFO_KEY = "DRIVER_INFO:";

    @Override
    public void driverInfoUpdate(DriverInfoUpdateReqDto reqDto) throws WaveException {
        // 查询用户
        DriverInfoDto driverInfo = getDriverInfoByAccount(reqDto.getAccount());
        // 更新车主信息
        DriverInfoEntity driverInfoEntity = new DriverInfoEntity();
        driverInfoEntity.setUserName(reqDto.getUserName());
        driverInfoEntity.setCarNo(reqDto.getCarNo());
        driverInfoEntity.setMobileNo(reqDto.getMobileNo());
        driverInfoEntity.setImageUrl(reqDto.getImageUrl());

        if (null == driverInfo || null == driverInfo.getDriverId()) {
            driverInfoDao.insert(driverInfoEntity);

            UpdateWrapper<AccountEntity> accountUpdateWrapper = new UpdateWrapper<>();
            accountUpdateWrapper.eq("account", reqDto.getAccount()).eq("status", 0);

            AccountEntity accountEntity = new AccountEntity();
            accountEntity.setDriverId(driverInfoEntity.getId());
            redissonClient.getBucket(DRIVER_INFO_KEY + reqDto.getAccount()).delete();
            accountDao.update(accountEntity, accountUpdateWrapper);
            return;
        }

        driverInfoEntity.setId(driverInfo.getDriverId());
        // 删除缓存
        redissonClient.getBucket(DRIVER_INFO_KEY + reqDto.getAccount()).delete();
        driverInfoDao.updateById(driverInfoEntity);
    }

    @Override
    public DriverInfoDto getDriverInfoByAccount(String account) throws WaveException {
        // 查缓存
        RBucket<DriverInfoDto> bucket = redissonClient.getBucket(DRIVER_INFO_KEY + account);
        DriverInfoDto driverInfoDto = bucket.get();
        if (null != driverInfoDto) {
            return driverInfoDto;
        }
        // 查driverId
        QueryWrapper<AccountEntity> accountQueryWrapper = new QueryWrapper<>();
        accountQueryWrapper.eq("account", account);
        accountQueryWrapper.eq("status", 0);
        List<AccountEntity> accountList = accountDao.selectList(accountQueryWrapper);
        if (CollectionUtils.isEmpty(accountList)) {
            throw new WaveException(WaveException.INVALID_PARAM, "无效账号");
        }
        if (accountList.size() > 1) {
            throw new WaveException(WaveException.SERVER_ERROR, "重复账号");
        }

        // 查driver信息
        Long driverId = accountList.get(0).getDriverId();
        DriverInfoEntity driverInfoEntity = driverInfoDao.selectById(driverId);
        driverInfoDto = new DriverInfoDto();
        if (null != driverInfoEntity) {
            BeanUtils.copyProperties(driverInfoEntity, driverInfoDto);
            driverInfoDto.setDriverId(driverInfoEntity.getId());
        }
        // 更新缓存
        bucket.set(driverInfoDto, RandomUtils.nextInt(60, 120), TimeUnit.MINUTES);
        return driverInfoDto;
    }
}
