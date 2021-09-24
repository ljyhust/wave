package com.play.dto;

import lombok.Data;

/**
 * @author zz
 * @date 2021/8/30 16:15
 */
@Data
public class PlayerScoreDto {

    /**
     * 玩家名称
     */
    private String player;

    /**
     * 玩家得分
     */
    private Integer score;

    public PlayerScoreDto(){}

    public PlayerScoreDto(String player, Integer score) {
        this.player = player;
        this.score = score;
    }

}
