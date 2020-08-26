package com.wave.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wave.user.dao.entity.UserInfoEntity;
import org.apache.ibatis.annotations.Options;

public interface UserInfoDao extends BaseMapper<UserInfoEntity>{

    /*@Override
    @Options(keyColumn = "id", useGeneratedKeys = true)
    int insert(UserInfoEntity entity);*/
}
