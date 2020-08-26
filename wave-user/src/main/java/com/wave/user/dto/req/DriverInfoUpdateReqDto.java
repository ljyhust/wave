package com.wave.user.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class DriverInfoUpdateReqDto implements Serializable{
    private static final long serialVersionUID = -8397823510504358776L;

    @NotBlank
    private String account;

    @NotBlank
    private String userName;

    @NotBlank
    private String mobileNo;

    @NotBlank
    private String carNo;

    private String imageUrl;
}
