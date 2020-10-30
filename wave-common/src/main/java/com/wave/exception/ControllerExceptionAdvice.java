package com.wave.exception;

import com.wave.common.PublicResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.sql.SQLTransientConnectionException;

@Slf4j
@ControllerAdvice
public class ControllerExceptionAdvice implements ResponseBodyAdvice {

    @ExceptionHandler(WaveException.class)
    @ResponseBody
    public PublicResponseDto processSystemException(WaveException ex) {
        PublicResponseDto resDto = new PublicResponseDto();
        resDto.setCode(ex.getErrCode());
        resDto.setMsg(ex.getErrMsg());
        return resDto;
    }

    @ExceptionHandler(SQLTransientConnectionException.class)
    @ResponseBody
    public PublicResponseDto processSqlConnectionException(SQLTransientConnectionException ex) {
        ex.printStackTrace();
        PublicResponseDto resDto = new PublicResponseDto();
        resDto.setCode(WaveException.OVER_THRESHOLD);
        resDto.setMsg("sorry， 服务繁忙，请扫稍后重试!");
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
    
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }
    
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass,
            ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (o instanceof PublicResponseDto) {
            PublicResponseDto res = (PublicResponseDto) o;
            if (res.getCode() != 200) {
                serverHttpResponse.setStatusCode(HttpStatus.resolve(res.getCode()));
            }
        }
        return o;
    }
}
