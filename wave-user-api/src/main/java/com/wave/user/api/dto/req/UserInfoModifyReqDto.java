package com.wave.user.api.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author lijinyang
 */
@Data
public class UserInfoModifyReqDto {

    private String userId;

    private String account;

    @NotBlank
    private String mobileNo;

}
