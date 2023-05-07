package com.yoyo.admin.redis_common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SampleRedisService {

    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setString(String key, String str) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set(key, str);
    }

    public String getString(String key) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Object object = operations.get(key);
        if (object instanceof String) {
            return (String) object;
        }
        return null;
    }

    public void setMap(String key, Map<?, ?> map) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set(key, map);
    }

    public Map<?, ?> getMap(String key) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Object object = operations.get(key);
        if (object instanceof Map) {
            return (Map<?, ?>) object;
        }
        return null;
    }

    public void setList(String key, List<?> list) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set(key, list);
    }

    public List<?> getList(String key) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Object object = operations.get(key);
        if (object instanceof List) {
            return (List<?>) object;
        }
        return null;
    }
}
