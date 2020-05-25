package com.wave.common;

import lombok.Data;

@Data
public class PublicResponseObjDto<T> extends PublicResponseDto{

    private T data;
}
