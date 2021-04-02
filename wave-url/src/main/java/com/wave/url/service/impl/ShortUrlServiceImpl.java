package com.wave.url.service.impl;

import com.alibaba.nacos.common.util.Md5Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wave.exception.WaveException;
import com.wave.url.dao.ShortUrlMapper;
import com.wave.url.dao.UrlIdMapper;
import com.wave.url.dao.entity.ShortUrlEntity;
import com.wave.url.dao.entity.UrlIdEntity;
import com.wave.url.service.ShortUrlService;
import com.wave.url.service.UrlIdService;
import com.wave.util.HexConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {
    
    @Autowired
    private ShortUrlMapper shortUrlMapper;
    
    @Autowired
    private UrlIdService urlIdService;
    
    @Override
    public String getShortUrl(String url) throws WaveException {
        // 找库判断是否存在url
        String md5 = Md5Utils.getMD5(url, "UTF-8");
        QueryWrapper<ShortUrlEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        List<ShortUrlEntity> shortUrlEntityList = shortUrlMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(shortUrlEntityList)) {
            if (shortUrlEntityList.size() > 1)
                throw new WaveException(WaveException.SERVER_ERROR, "原生url存在多个");
            return HexConvertUtils.dicTo36Hex(shortUrlEntityList.get(0).getId());
        }
        // 不存在
        long urlId = urlIdService.urlIdGenerate();
        ShortUrlEntity shortUrlEntity = new ShortUrlEntity();
        shortUrlEntity.setOriginalUrl(url);
        shortUrlEntity.setId(urlId);
        shortUrlEntity.setMd5(md5);
        shortUrlMapper.insert(shortUrlEntity);
        return HexConvertUtils.dicTo36Hex(urlId);
    }
    
}
