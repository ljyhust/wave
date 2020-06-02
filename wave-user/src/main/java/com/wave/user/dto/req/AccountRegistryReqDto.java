package com.wave.user.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author lijinyang
 */
@Data
public class AccountRegistryReqDto {

    @NotBlank
    private String account;

    private short type;
}
