package com.wave.user.dto.vo;

import com.wave.user.api.dto.UserInfoDto;
import lombok.Data;

import java.util.List;

@Data
public class MyConcernUserVo {

    private Long userId;

    private List<UserInfoDto> concernUserList;

}
