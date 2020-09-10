package com.wave.user.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoDto implements Serializable{

    private static final long serialVersionUID = -2610990151763414181L;
    private String nickName;

    private Long userId;

    private String mobile;

    private String imageUrl;

    private String email;
}
