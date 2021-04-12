package com.wave.url.service.impl;

import com.wave.url.dao.UrlIdMapper;
import com.wave.url.dao.entity.UrlIdEntity;
import com.wave.url.service.UrlIdService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class UrlIdServiceImpl implements UrlIdService {
    
    private volatile AtomicLong idStart;
    
    private volatile long maxId;
    
    @Autowired
    private UrlIdMapper urlIdMapper;
    
    @Override
    public long urlIdGenerate() {
        if (null == idStart || idStart.longValue() > maxId) {
            ((UrlIdServiceImpl) AopContext.currentProxy()).initIdStart();
        }
        return idStart.getAndIncrement();
    }
    
    @Transactional
    @Override
    public void initIdStart() {
        if (null != idStart) {
            return;
        }
        UrlIdEntity urlIdEntity = new UrlIdEntity();
        urlIdEntity.setStartNum(0L);
        urlIdEntity.setEndNum(0L);
        urlIdMapper.insert(urlIdEntity);
        urlIdEntity.setEndNum(urlIdEntity.getId() * 1000);
        urlIdEntity.setStartNum(urlIdEntity.getEndNum() - 999);
        urlIdMapper.updateById(urlIdEntity);
        idStart = new AtomicLong(urlIdEntity.getStartNum());
        maxId = urlIdEntity.getEndNum();
    }
}
