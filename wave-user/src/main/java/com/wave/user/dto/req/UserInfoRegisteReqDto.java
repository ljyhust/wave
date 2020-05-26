package com.wave.user.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserInfoRegisteReqDto {

    private String userId;

    @NotBlank
    private String mobileNo;

    private String password;

}
