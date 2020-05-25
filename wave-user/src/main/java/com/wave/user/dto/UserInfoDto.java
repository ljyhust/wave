package com.wave.user.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoDto implements Serializable{

    private static final long serialVersionUID = -2610990151763414181L;
    private String userName;

    private String userId;

    private String mobileNo;

    private String imageUrl;
}
