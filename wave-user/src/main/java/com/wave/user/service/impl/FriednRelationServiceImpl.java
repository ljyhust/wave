package com.wave.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wave.common.WaveConstants;
import com.wave.exception.WaveException;
import com.wave.user.api.dto.UserInfoDto;
import com.wave.user.dao.UserConcernDao;
import com.wave.user.dao.UserFancyDao;
import com.wave.user.dao.UserInfoDao;
import com.wave.user.dao.entity.UserConcernEntity;
import com.wave.user.dao.entity.UserInfoEntity;
import com.wave.user.dto.vo.MyConcernUserVo;
import com.wave.user.dto.vo.MyFancyUserVo;
import com.wave.user.service.FriendRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FriednRelationServiceImpl implements FriendRelationService{
    @Autowired
    UserConcernDao userConcernDao;

    @Autowired
    UserFancyDao userFancyDao;

    @Autowired
    UserInfoDao userInfoDao;

    @Override
    public MyConcernUserVo queryMyConcernUserInfo(Long userId) throws WaveException {
        List<UserInfoDto> userInfos = getFriendUserInfos(userId, "concernUserId", userConcernDao);
        // res vo
        MyConcernUserVo myConcernUserVo = new MyConcernUserVo();
        myConcernUserVo.setUserId(userId);
        if (CollectionUtils.isEmpty(userInfos)) {
            return myConcernUserVo;
        }

        myConcernUserVo.setConcernUserList(userInfos);
        return myConcernUserVo;
    }

    @Override
    public MyFancyUserVo queryMyFancyUserInfo(Long userId) throws WaveException {
        List<UserInfoDto> userInfos = getFriendUserInfos(userId, "fancyUserId", userConcernDao);
        // res vo
        MyFancyUserVo myFancyUserVo = new MyFancyUserVo();
        myFancyUserVo.setUserId(userId);
        if (CollectionUtils.isEmpty(userInfos)) {
            return myFancyUserVo;
        }

        myFancyUserVo.setFancyUserList(userInfos);
        return myFancyUserVo;
    }

    private <T> List<UserInfoDto> getFriendUserInfos(Long userId, String idName, BaseMapper<T> baseMapper) {
        // 获取concernUserIds
        QueryWrapper<T> userConcernEntityQueryWrapper = new QueryWrapper<>();
        userConcernEntityQueryWrapper.eq("user_id", userId);
        userConcernEntityQueryWrapper.eq("status", WaveConstants.NORMAL_STATUS);
        List<T> userConcernEntities = baseMapper.selectList(userConcernEntityQueryWrapper);

        // 批量查询用户信息
        List<Long> concernUserIds = userConcernEntities.stream().map(e -> {
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(e));
            return jsonObject.getLong(idName);
        }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(concernUserIds)) {
            return null;
        }

        // 批量查询的分库分表是怎么做的？
        List<UserInfoEntity> userInfoEntities = userInfoDao.selectBatchIds(concernUserIds);

        List<UserInfoDto> concernUserInfos = new ArrayList<>();

        userInfoEntities.forEach(e -> {
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setUserId(e.getId());
            userInfoDto.setImageUrl(e.getImageUrl());
            userInfoDto.setNickName(e.getNickName());
            //userInfoDto.setMobile(e.getMobile());
            //userInfoDto.setEmail(e.getEmail());
            concernUserInfos.add(userInfoDto);
        });
        return concernUserInfos;
    }
}
