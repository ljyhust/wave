package com.wave.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wave.base.dao.DictDao;
import com.wave.base.dao.entity.DictEntity;
import com.wave.base.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DictServiceImpl implements DictService{
    @Autowired
    DictDao dictDao;
    @Override
    public Object test() {
        QueryWrapper<DictEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        return dictDao.selectList(wrapper);
    }
}
