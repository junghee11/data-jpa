package com.develop.websocket.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    private String toJson(Object value) {
        if (value instanceof String s) {
            return s;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Redis 직렬화 실패: " + value.getClass().getSimpleName(), e);
        }
    }

    private <T> T fromJson(String json, Class<T> type) {
        if (json == null) {
            return null;
        }
        if (type == String.class) {
            return type.cast(json);
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Redis 역직렬화 실패: " + type.getSimpleName(), e);
        }
    }

    public void save(String key, Object value) {
        stringRedisTemplate.opsForValue().set(key, toJson(value));
    }

    public void save(String key, Object value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, toJson(value), timeout, unit);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public <T> T get(String key, Class<T> type) {
        return fromJson(get(key), type);
    }

    public void delete(String key) {
        stringRedisTemplate.unlink(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    public void addToSet(String key, String... values) {
        stringRedisTemplate.opsForSet().add(key, values);
    }

    public Set<String> getSet(String key) {
        Set<String> members = stringRedisTemplate.opsForSet().members(key);
        return members != null ? members : Collections.emptySet();
    }

    public void removeFromSet(String key, String... values) {
        stringRedisTemplate.opsForSet().remove(key, (Object[]) values);
    }

    public boolean isMember(String key, String value) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(key, value));
    }

    public void saveToHash(String key, String hashKey, Object value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, toJson(value));
    }

    public <T> T getFromHash(String key, String hashKey, Class<T> type) {
        Object value = stringRedisTemplate.opsForHash().get(key, hashKey);
        return fromJson(value != null ? value.toString() : null, type);
    }

    public Map<Object, Object> getAllFromHash(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public void deleteFromHash(String key, Object... hashKeys) {
        stringRedisTemplate.opsForHash().delete(key, hashKeys);
    }

    public void pushToList(String key, Object value) {
        stringRedisTemplate.opsForList().rightPush(key, toJson(value));
    }

    public String popFromList(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    public <T> List<T> getList(String key, long start, long end, Class<T> type) {
        List<String> range = stringRedisTemplate.opsForList().range(key, start, end);
        if (range == null) {
            return Collections.emptyList();
        }
        return range.stream().map(json -> fromJson(json, type)).toList();
    }

    public Long getListSize(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    public void expire(String key, long timeout, TimeUnit unit) {
        stringRedisTemplate.expire(key, timeout, unit);
    }

    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    public Long decrement(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    public Set<String> getKeys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    public void deleteKeys(String pattern) {
        Set<String> keys = getKeys(pattern);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.unlink(keys);
        }
    }

}
