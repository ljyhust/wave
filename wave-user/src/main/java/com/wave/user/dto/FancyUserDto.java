package com.wave.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class FancyUserDto {

    private Long userId;

    private List<Long> fancyUserIds;
}
