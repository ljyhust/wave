package com.play.dto;

import lombok.Data;

/**
 * @author zz
 * @date 2021/8/30 16:18
 */
@Data
public class PlayerRankVo {
    /**
     * 玩家名称
     */
    private String player;

    /**
     * 玩家排名
     */
    private Integer rank;

    public PlayerRankVo(String player, Integer rank) {
        this.player = player;
        this.rank = rank;
    }
}
