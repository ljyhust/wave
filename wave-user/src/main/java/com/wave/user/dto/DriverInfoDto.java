package com.wave.user.dto;

import com.wave.user.api.dto.UserInfoDto;
import lombok.Data;

import java.io.Serializable;

@Data
public class DriverInfoDto extends UserInfoDto implements Serializable{
    private static final long serialVersionUID = 1056057651037812180L;

    private Long driverId;

    private String carNo;

    private String idNum;
}
