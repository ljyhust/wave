package com.wave.user.dto.vo;

import com.wave.user.api.dto.UserInfoDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MyFancyUserVo implements Serializable {
    
    private static final long serialVersionUID = -7823327996789925011L;
    
    private Long userId;

    private List<UserInfoDto> fancyUserList;
}
