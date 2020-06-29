package com.wave.operator;

import lombok.Data;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * redis实现延时队列
 * Created by lijinyang on 2020/5/27.
 */
@Data
public class DelayQueueRedisOpt<T>{

    private RedissonClient redissonClient;

    public DelayQueueRedisOpt() {}

    public DelayQueueRedisOpt(RedissonClient client) {
        this.redissonClient = client;
    }

    /**
     * 添加元素及分数，对应redis命令 zadd key score1 val1
     * @param key
     * @param <T>
     * @return
     */
    public boolean add(String key, T val, Double score) {
        RScoredSortedSet<T> sortedSet = redissonClient.getScoredSortedSet(key);
        return sortedSet.add(score, val);
    }

    /**
     * 批量增加
     * @param key
     * @param map
     * @param <T>
     * @return
     */
    public int addBatch(String key, Map<T, Double> map) {
        if (CollectionUtils.isEmpty(map)) {
            return 0;
        }
        RScoredSortedSet<T> sortedSet = redissonClient.getScoredSortedSet(key);

        return sortedSet.addAll(map);
    }

    /**
     * zrangebyscore 先取出
     * zremrangebyscore 后删除
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Collection<T> pollByScore(String key, double min, double max) {
        RScoredSortedSet<T> sortedSet = redissonClient.getScoredSortedSet(key);
        Collection<T> ts = sortedSet.valueRange(min, true, max, true);
        // 删除已消费的元素
        sortedSet.removeAll(ts);
        return ts;
    }

    public void batchDeleteSetObjs(String key, Collection<T> objects) {
        RScoredSortedSet<Object> scoredSortedSet = redissonClient.getScoredSortedSet(key);
        scoredSortedSet.removeAll(objects);
    }
}
