package com.wave.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wave.common.PageVo;
import com.wave.common.WaveConstants;
import com.wave.exception.WaveException;
import com.wave.user.dao.UserAccountDao;
import com.wave.user.dao.UserConcernDao;
import com.wave.user.dao.UserInfoDao;
import com.wave.user.dao.entity.AccountEntity;
import com.wave.user.dao.entity.UserConcernEntity;
import com.wave.user.dao.entity.UserInfoEntity;
import com.wave.user.api.dto.UserInfoDto;
import com.wave.user.dto.req.UserInfoModifyReqDto;
import com.wave.user.dto.req.UserInfoUpdateReqDto;
import com.wave.user.service.UserInfoService;
import org.apache.commons.lang3.RandomUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserInfoServiceImpl implements UserInfoService{

    @Autowired
    UserInfoDao userInfoDao;

    @Autowired
    UserAccountDao userAccountDao;

    @Autowired
    RedissonClient redissonClient;
    
    @Autowired
    UserConcernDao userConcernDao;

    private final String USER_INFO_KEY = "USER_INFO:";

    @Override
    public void registerUser(UserInfoModifyReqDto registryReqDto) throws WaveException {
        // 查询是否有同名mobile
        QueryWrapper<UserInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile_no", registryReqDto.getMobileNo()).eq("status", WaveConstants.NORMAL_STATUS);
        List<UserInfoEntity> entityList = userInfoDao.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(entityList)) {
            throw new WaveException(WaveException.INVALID_PARAM, "账号已注册");
        }
        // 如有返回错误
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setMobile(registryReqDto.getMobileNo());
        userInfoEntity.setNickName(registryReqDto.getMobileNo());
        userInfoDao.insert(userInfoEntity);
    }

    @Override
    public void userInfoUpdate(UserInfoUpdateReqDto reqDto) throws WaveException {
        // 查询userId，如果没有，则增加用户信息
        UserInfoDto userInfo = getUserInfoByAccount(reqDto.getAccount());
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setMobile(reqDto.getMobileNo());
        userInfoEntity.setNickName(reqDto.getMobileNo());
        userInfoEntity.setImageUrl(reqDto.getImageUrl());
        if (null == userInfo || null == userInfo.getUserId()) {
            // 新增用户信息
            userInfoDao.insert(userInfoEntity);
            // 更新account信息
            UpdateWrapper<AccountEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("account", reqDto.getAccount()).eq("status", 0);

            AccountEntity accountEntity = new AccountEntity();
            accountEntity.setUserId(userInfoEntity.getId());
            // 删除缓存
            redissonClient.getBucket(USER_INFO_KEY + reqDto.getAccount()).delete();
            userAccountDao.update(accountEntity, updateWrapper);
            return;
        }
        userInfoEntity.setId(userInfo.getUserId());
        // 更新用户信息，删除缓存
        redissonClient.getBucket(USER_INFO_KEY + userInfo.getUserId()).delete();
        userInfoDao.updateById(userInfoEntity);
    }

    @Override
    public void editUserInfo(UserInfoDto dto) throws WaveException {
        // 查询id是否存在，存在则 删缓存 更新
        if (null == dto.getUserId()) {
            throw new WaveException(WaveException.INVALID_PARAM, "userId不能为空");
        }
        RBucket<UserInfoDto> bucket = redissonClient.getBucket(USER_INFO_KEY + dto.getUserId());
        UserInfoDto userInfoCache = bucket.get();
        // existUser true-不存在  false-存在
        boolean existUser = null == userInfoCache;
        if (existUser) {
            // 不存在 查库
            UserInfoEntity entity = userInfoDao.selectById(dto.getUserId());
            existUser = null == entity;
        }
        // 不存在则添加
        UserInfoEntity userInfoEntityEdit = new UserInfoEntity();
        userInfoEntityEdit.setNickName(dto.getNickName());
        userInfoEntityEdit.setMobile(dto.getMobile());
        userInfoEntityEdit.setEmail(dto.getEmail());
        userInfoEntityEdit.setImageUrl(dto.getImageUrl());
        if (existUser) {
            userInfoDao.insert(userInfoEntityEdit);
        } else {
            // 删除缓存后更新
            bucket.delete();
            userInfoEntityEdit.setId(dto.getUserId());
            userInfoDao.updateById(userInfoEntityEdit);
        }
    }
    
    @Override
    public UserInfoDto getUserInfo(String userId) throws WaveException {
        // 查询缓存
        RBucket<UserInfoDto> bucket = redissonClient.getBucket(USER_INFO_KEY + userId);
        UserInfoDto userInfoDto = bucket.get();
        if (null != userInfoDto) {
            return userInfoDto;
        }
        QueryWrapper<UserInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", Long.valueOf(userId)).eq("status", WaveConstants.NORMAL_STATUS);
        List<UserInfoEntity> entityList = userInfoDao.selectList(queryWrapper);
        boolean empty = CollectionUtils.isEmpty(entityList);
        if (!empty && entityList.size() > 1) {
            throw new WaveException(WaveException.SERVER_ERROR, "多重账号");
        }
        UserInfoDto resDto = new UserInfoDto();
        if (!empty) {
            BeanUtils.copyProperties(entityList.get(0), resDto);
            resDto.setUserId(entityList.get(0).getId());
        }
        // 放入缓存，异步？  resDt为空也放入缓存，防止缓存穿透
        bucket.set(resDto, RandomUtils.nextInt(60, 120), TimeUnit.MINUTES);
        return resDto;
    }

    @Override
    public UserInfoDto getUserInfoByAccount(String account) throws WaveException {
        // 查询缓存
        RBucket<UserInfoDto> bucket = redissonClient.getBucket(USER_INFO_KEY + account);
        UserInfoDto userInfoDto = bucket.get();
        if (null != userInfoDto) {
            return userInfoDto;
        }
        // 查库获取userId
        QueryWrapper<AccountEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        queryWrapper.eq("status", WaveConstants.NORMAL_STATUS);
        List<AccountEntity> accounts = userAccountDao.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(accounts)) {
            throw new WaveException(WaveException.INVALID_PARAM, "用户未注册");
        }

        // 取第一个uid不为null的
        Long userId = null;
        AccountEntity accountInfo = null;
        for (AccountEntity accountEntity : accounts) {
            if (null != accountEntity.getUserId()) {
                userId = accountEntity.getUserId();
                accountInfo = accountEntity;
                break;
            }
        }

        if (null == userId) {
            throw new WaveException(WaveException.INVALID_PARAM, "用户未注册");
        }
        // 查询user
        userInfoDto = getUserInfo(userId.toString());
        bucket.set(userInfoDto, RandomUtils.nextInt(60, 120), TimeUnit.MINUTES);
        // FIXME 异步？去除其它无效的account
        UpdateWrapper<AccountEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account", account);
        updateWrapper.ne("id", accountInfo.getId());

        AccountEntity updateAccount = new AccountEntity();
        updateAccount.setStatus(WaveConstants.DEL_STATUS);
        userAccountDao.update(updateAccount, updateWrapper);
        return userInfoDto;
    }
}
