package com.wave.url.service;

import com.wave.exception.WaveException;

public interface ShortUrlService {
    
    /**
     * 通过源url返回短链接
     * @param url
     * @return
     */
    String getShortUrl(String url) throws WaveException;
}
