package com.wave.common;

import lombok.Data;

@Data
public class PublicResponseDto {

    public PublicResponseDto() {

    }

    public PublicResponseDto(int code) {
        this.code = code;
    }

    public PublicResponseDto(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;

    private String msg;

}
