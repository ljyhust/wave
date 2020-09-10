package com.wave.user.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserInfoEditReqDto {

    @NotBlank(message = "must not be null")
    private String nickName;

    private String imgUrl;

    private String email;

    private String mobile;
}
