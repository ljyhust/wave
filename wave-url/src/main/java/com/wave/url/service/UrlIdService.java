package com.wave.url.service;

public interface UrlIdService {
    
    /**
     * 生成链接id
     * @return
     */
    long urlIdGenerate();
    
    void initIdStart();
}
