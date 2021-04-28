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
    public synchronized void initIdStart() {
        // 如果觉得synchronized锁不够好，因为存在远程调用，还有一种方案是 CAS + 版本号的方法
        // 开启事务之前，我们是认为当前 maxId / 1000 == 数据库的最大id的
        // oldVersion = maxId / 1000;
        // if (idStart.longValue() < oldVersion) return;
        // insert添加id = oloVersion + 1的，如果失败，则重试几次，但容易活锁
        if (null != idStart && idStart.longValue() <= maxId) {
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
