package com.wave.exception;

import com.wave.common.PublicResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(WaveException.class)
    @ResponseBody
    public PublicResponseDto processSystemException(WaveException ex) {
        PublicResponseDto resDto = new PublicResponseDto();
        resDto.setCode(ex.getErrCode());
        resDto.setMsg(ex.getErrMsg());
        return resDto;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public PublicResponseDto processCommonThrowable(Throwable ex) {
        log.info("====> controller response exception {}", ex);
        PublicResponseDto resDto = new PublicResponseDto();
        resDto.setCode(WaveException.SERVER_ERROR);
        try {
            String error = StringUtils.isBlank(ex.getMessage()) ? ex.getCause().getMessage() : ex.getMessage();
            resDto.setMsg(error);
        } catch (Exception e) {
            log.error("====>  错误拦截 {}", ex);
        }
        return resDto;
    }
}
