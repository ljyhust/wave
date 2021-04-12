package com.wave.url.web;

import com.wave.common.PublicResponseDto;
import com.wave.common.PublicResponseObjDto;
import com.wave.common.PublicResponseUtil;
import com.wave.exception.WaveException;
import com.wave.url.pojo.ShortUrlGenRequest;
import com.wave.url.pojo.ShortUrlGenResponse;
import com.wave.url.service.ShortUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("urlFactory")
@RestController
public class ShortUrlController {
    
    @Autowired
    private ShortUrlService shortUrlService;
    
    /**
     * short url
     * @param request
     * @return
     */
    @PostMapping("shortUrlGen")
    public PublicResponseObjDto genShortUrl(@RequestBody @Validated ShortUrlGenRequest request) throws WaveException {
        String shortUrl = shortUrlService.getShortUrl(request.getOriginalUrl());
        ShortUrlGenResponse genResponse = new ShortUrlGenResponse();
        genResponse.setShortUrl(shortUrl);
        return PublicResponseUtil.okPublicResponseObjDto(genResponse);
    }
}
