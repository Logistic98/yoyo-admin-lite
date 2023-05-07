package com.yoyo.admin.redis_common.service;

import com.yoyo.admin.redis_common.annotation.RedisEntity;
import com.yoyo.admin.redis_common.annotation.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //获取实体类型Key模板
    public String getEntityTemplate(Class<?> typeClass) {
        RedisEntity redisEntity = typeClass.getAnnotation(RedisEntity.class);
        if (redisEntity == null) {
            return null;
        }
        if (!redisEntity.keyTemplate().isEmpty()) {
            return redisEntity.keyTemplate();
        } else {
            return typeClass.getName();
        }
    }

    //从实体定义中获取key
    private <T> String getEntityKey(T t) throws IllegalAccessException {
        Class<?> typeClass = t.getClass();
        RedisEntity redisEntity = typeClass.getAnnotation(RedisEntity.class);
        if (redisEntity == null) {
            return null;
        }
        if (!redisEntity.keyTemplate().isEmpty()) {
            String template = redisEntity.keyTemplate();
            LinkedList<Integer> orders = new LinkedList<>();
            LinkedList<String> keyParameters = new LinkedList<>();
            Field[] fields = typeClass.getDeclaredFields();
            if (fields.length > 0) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    RedisKey redisKey = field.getAnnotation(RedisKey.class);
                    if (redisKey != null) {
                        int order = redisKey.order();
                        boolean inserted = false;
                        for (int i = 0; i <= orders.size() - 1; i++) {
                            if (order < orders.get(i)) {
                                orders.add(i, order);
                                keyParameters.add(i, field.get(t) != null ? field.get(t).toString() : "");
                                inserted = true;
                                break;
                            }
                        }
                        if (!inserted) {
                            orders.addLast(order);
                            keyParameters.addLast(field.get(t) != null ? field.get(t).toString() : "");
                        }
                    }
                }
            }
            if (keyParameters.size() > 0) {
                return String.format(template, keyParameters.toArray());
            } else {
                return template;
            }
        } else {
            return typeClass.getName();
        }
    }

    //从实体定义中获取过期时间
    private <T> Long getEntityExpire(T t) {
        Class<?> typeClass = t.getClass();
        RedisEntity redisEntity = typeClass.getAnnotation(RedisEntity.class);
        if (redisEntity == null || redisEntity.expire() <= 0) {
            return null;
        }
        return redisEntity.expire();
    }

    //向redis写入值，使用实体定义中的key
    public String set(Object t) throws IllegalAccessException, RuntimeException {
        String key = getEntityKey(t);
        if (key == null) {
            throw new RuntimeException("Redis Set : Type has no @RedisEntity annotation");
        }
        Long expire = getEntityExpire(t);
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set(key, t);
        if (expire != null) {
            expire(key, expire);
        }
        return key;
    }

    //向redis写入值，使用实体定义中的key
    public String set(String key,Object t) throws IllegalAccessException, RuntimeException {
        if (key == null) {
            throw new RuntimeException("Redis Set : Type has no @RedisEntity annotation");
        }
        Long expire = getEntityExpire(t);
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set(key, t);
        if (expire != null) {
            expire(key, expire);
        }
        return key;
    }

    //从redis中按照实体定义使用指定参数生成key以得到value
    public <T> T get(Class<T> tClass, Object... keyParams) throws RuntimeException {
        RedisEntity redisEntity = tClass.getAnnotation(RedisEntity.class);
        if (redisEntity == null) {
            throw new RuntimeException("Redis Get : Type has no @RedisEntity annotation");
        }
        String template = redisEntity.keyTemplate();
        if (keyParams != null && keyParams.length > 0) {
            String key = String.format(template, keyParams);
            return getForType(tClass, key);
        } else {
            return getForType(tClass, template);
        }
    }

    //从redis中按照实体定义使用指定参数生成key以得到value
    public <T> T get(Class<T> tClass, String key) throws RuntimeException {
            return getForType(tClass, key);
    }

    //从redis中使用指定key以得到特定类型的value
    public <T> T getForType(Class<T> type, String key) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Object object = operations.get(key);
        if (object != null && type.equals(object.getClass())) {
            return type.cast(object);
        }
        return null;
    }

    //从redis中使用指定key集合以得到特定类型的value集合
    public <T> List<T> listForType(Class<T> type, List<String> keys) {
        List<T> list = new ArrayList<>();
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        List<Object> objects = operations.multiGet(keys);
        if (objects == null || objects.size() == 0) {
            return list;
        }
        for (int i = 0; i <= objects.size() - 1; i++) {
            Object object = objects.get(i);
            if (object != null && type.equals(object.getClass())) {
                list.add(type.cast(object));
            }
        }
        return list;
    }

    //使用实体设置的模板获取实体所有的key
    public <T> Set<String> keys(Class<T> tClass, Object... keyParams) throws RuntimeException {
        RedisEntity redisEntity = tClass.getAnnotation(RedisEntity.class);
        if (redisEntity == null) {
            throw new RuntimeException("Redis Keys(Class<T>,Object...) : Type has no @RedisEntity annotation");
        }
        String template = redisEntity.keyTemplate();
        String pattern = String.format(template, keyParams);
        return keys(pattern);
    }

    //使用实体设置的模板获取实体所有的key
    public <T> Set<String> keys(Class<T> tClass) throws RuntimeException {
        RedisEntity redisEntity = tClass.getAnnotation(RedisEntity.class);
        if (redisEntity == null) {
            throw new RuntimeException("Redis Keys(Class<T>) : Type has no @RedisEntity annotation");
        }
        String template = redisEntity.keyTemplate();
        String pattern = template.substring(0, template.indexOf("%"));
        return keys(pattern + "*");
    }

    //获取所有匹配的key
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    //使用实体设备的模板删除实体
    public <T> void delete(Class<T> tClass, Object... keyParams) throws RuntimeException {
        RedisEntity redisEntity = tClass.getAnnotation(RedisEntity.class);
        if (redisEntity == null) {
            throw new RuntimeException("Redis Delete : Type has no @RedisEntity annotation");
        }
        String template = redisEntity.keyTemplate();
        String key = String.format(template, keyParams);
        delete(key);
    }

    //删除key
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    //批量删除key
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    //为指定Key设置过期时间，单位秒
    public void expire(String key, long expire) {
        redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    //判断key是否存在
    public <T> Boolean hasKey(Class<T> tClass, Object... keyParams) {
        RedisEntity redisEntity = tClass.getAnnotation(RedisEntity.class);
        if (redisEntity == null) {
            return false;
        }
        String template = redisEntity.keyTemplate();
        return hasKey(String.format(template, keyParams));
    }

    //判断key是否存在
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

}
