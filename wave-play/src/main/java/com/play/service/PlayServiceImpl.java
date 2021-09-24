package com.play.service;

import com.play.dto.PlayerInputVo;
import com.play.dto.PlayerRankVo;
import com.play.dto.PlayerScoreDto;
import com.play.external.TopKHeap;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zz
 * @date 2021/8/30 16:20
 * 玩家游戏业务类
 */
@Service
public class PlayServiceImpl implements PlayServiceI{

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    TopKHeap topKHeap;

    @Override
    public Integer inputCircleScore(PlayerInputVo playerInput) {
        int score = maxLenStringSymmetry(playerInput.getInput());
        // 添加进入排序
        topKHeap.add(new PlayerScoreDto(playerInput.getPlayer(), score));
        return score;
    }

    @Override
    public List<PlayerRankVo> playerRank() {
        return topKHeap.getRank();
    }

    /**
     * 最长回文对称串长度计算
     * @return
     */
    private int maxLenStringSymmetry(String text) {
        if (StringUtils.isBlank(text)) {
            return 0;
        }
        int max = 0;
        for (int i = 0; i < text.length(); i++) {
            // charAt(i-1) == charAt(i + 1)
            int len1 = centerExtend(text, i, i);
            // charAt(i) == charAt(i + 1)
            int len2 = centerExtend(text, i, i + 1);
            int len = Math.max(len1, len2);
            if (len > max) {
                max = len;
            }
        }
        return max;
    }

    /**
     * 由中心向左右计算长度
     * @param s 字符串
     * @param l 左指针
     * @param r 右指针
     * @return
     */
    private int centerExtend(String s, int l, int r) {
        while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
            --l;
            ++r;
        }
        return r - l - 1;
    }
}
