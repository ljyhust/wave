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
}
