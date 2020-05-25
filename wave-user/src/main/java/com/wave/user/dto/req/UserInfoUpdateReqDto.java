package com.wave.user.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserInfoUpdateReqDto {

    @NotBlank
    private String userName;

    private String userId;

    @NotBlank
    private String mobileNo;

    @NotBlank
    private String imageUrl;

}
