package com.play.service;

import com.play.dto.PlayerInputVo;
import com.play.dto.PlayerRankVo;

import java.util.List;

/**
 * @author zz
 * @date 2021/8/30 16:13
 */
public interface PlayServiceI {

    /**
     * 计算回文串得分
     * @param playerInput
     * @return
     */
    Integer inputCircleScore(PlayerInputVo playerInput);

    /**
     * 玩家排名
     * @return
     */
    List<PlayerRankVo> playerRank();
}
