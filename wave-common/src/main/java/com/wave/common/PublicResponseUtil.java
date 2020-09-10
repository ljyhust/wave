package com.wave.common;

public class PublicResponseUtil {

    public static PublicResponseDto publicResponseDto() {
        return new PublicResponseDto(200);
    }

    public static <T> PublicResponseObjDto okPublicResponseObjDto(T data) {
        PublicResponseObjDto<T> resDto = new PublicResponseObjDto<>();
        resDto.setCode(200);
        resDto.setData(data);
        return resDto;
    }

    public static <T> RestResult okRestResult(T data) {
        RestResult<T> result = new RestResult<>();
        result.setCode(200);
        result.setData(data);
        return result;
    }

    public static RestResult okRestResult() {
        RestResult<Object> result = new RestResult<>();
        result.setCode(200);
        return result;
    }
}
