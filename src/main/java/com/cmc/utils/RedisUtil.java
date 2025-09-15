package com.cmc.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存并指定过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取缓存
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     */
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取过期时间
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * Hash 设置值
     * @param key Redis key
     * @param hashKey Hash field
     * @param value 值
     * @param expireSeconds 可选过期时间（秒），如果 <=0 则不设置
     */
    public void hashPut(String key, String hashKey, Object value, long expireSeconds) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        if (expireSeconds > 0) {
            redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
        }
    }

    public void hashPut(String key, String hashKey, Object value) {
        hashPut(key, hashKey, value, 0);
    }

    /**
     * Hash 获取值
     * @param key Redis key
     * @param hashKey Hash field
     * @return 对应值
     */
    public Object hashGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 删除 Hash 中某个字段
     * @param key Redis key
     * @param hashKey Hash field
     */
    public void hashDelete(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

}
