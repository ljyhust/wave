package com.play.external;

import com.play.dto.PlayerRankVo;
import com.play.dto.PlayerScoreDto;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zz
 * @date 2021/8/30 16:58
 * top k排序，利用堆排序计算
 * 单机下可用Java优先队列实现；
 * 高并发可能需要多实例，则可采用集群redis维护，但数据不可存储太多，需要限制大小
 */
@Service
public class TopKHeap {

    private final String ZSET_KEY = "PLAYER_RANK";

    @Value("${topK.size:10}")
    private int size;

    @Autowired
    RedissonClient redissonClient;

    /**
     * 添加玩家得分到redis zset中
     * 并判断zset大小，并删除size之外的元素
     * @param playerScore
     */
    public void add(PlayerScoreDto playerScore) {
        String script = "redis.call('zadd', KEYS[1], ARGV[1], ARGV[2]);" +
                // 判断大小，并删除 0， -10 的数据
                String.format("if (redis.call('zcard', KEYS[1]) > %d) then redis.call('ZREMRANGEBYRANK', KEYS[1], 0, %d); end;", size, -size);
        redissonClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.STATUS, Collections.singletonList(ZSET_KEY),
                playerScore.getScore(), playerScore.getPlayer());
    }

    /**
     * 获取排名
     * 通过 ZREVRANGE 命令获取从大到小 0 ~ size的元素
     * @return
     */
    public List<PlayerRankVo> getRank() {
        String script = "redis.call('ZREVRANGE', KEYS[1], ARGV[1], ARGV[2])";
        Object eval = redissonClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_ONLY, script, RScript.ReturnType.MULTI, Collections.singletonList(ZSET_KEY),
                0, size);
        List<String> list = (List) eval;
        List<PlayerRankVo> resList = new ArrayList<>();
        for (int i = 0; i < list.size(); i += 2) {
            resList.add(new PlayerRankVo(list.get(i), Integer.valueOf(list.get(2))));
        }
        return resList;
    }
}
