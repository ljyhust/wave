package com.wave.user.api.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserInfoUpdateReqDto {

    @NotBlank
    private String account;

    @NotBlank
    private String userName;

    @NotBlank
    private String mobileNo;

    @NotBlank
    private String imageUrl;

}
