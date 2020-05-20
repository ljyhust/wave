package com.wave.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wave.common.WaveConstants;
import com.wave.exception.WaveException;
import com.wave.user.dao.UserInfoDao;
import com.wave.user.dao.entity.UserInfoEntity;
import com.wave.user.dto.req.UserRegistryReqDto;
import com.wave.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService{

    @Autowired
    UserInfoDao userInfoDao;

    @Override
    public void registerUser(UserRegistryReqDto registryReqDto) throws WaveException {
        // 查询是否有同名userId
        QueryWrapper<UserInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", registryReqDto.getUserId()).eq("status", WaveConstants.NORMAL_STATUS);
        List<UserInfoEntity> entityList = userInfoDao.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(entityList)) {
            throw new WaveException(WaveException.INVALID_PARAM, "账号已注册");
        }
        // 如有返回错误
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setMobileNo(registryReqDto.getMobileNo());
        userInfoEntity.setUserId(registryReqDto.getUserId());
        userInfoEntity.setMobileNo(registryReqDto.getMobileNo());
        userInfoDao.insert(userInfoEntity);
    }
}
