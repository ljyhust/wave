package com.play.dto;

import lombok.Data;

/**
 * @author zz
 * @date 2021/8/30 16:13
 */
@Data
public class PlayerInputVo {

    /**
     * 玩家名称
     */
    private String player;

    /**
     * 玩家输入字符串
     */
    private String input;
}
