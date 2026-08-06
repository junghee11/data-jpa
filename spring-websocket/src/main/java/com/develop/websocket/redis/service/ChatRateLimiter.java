package com.develop.websocket.redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRateLimiter {

    private static final String RATE_LIMIT_PREFIX = "rate:chat:";

    private static final long WINDOW_SECONDS = 10;
    private static final long LIMIT_COUNT = 20;

    private final RedisService redisService;

    public boolean isExceeded(String userId) {
        String key = RATE_LIMIT_PREFIX + userId;

        Long count = redisService.increment(key);

        // 윈도우의 첫 메시지일 때만 TTL 설정 → 10초 지나면 키가 사라지며 카운트 리셋
        if (count != null && count == 1) {
            redisService.expire(key, WINDOW_SECONDS, TimeUnit.SECONDS);
        }

        boolean exceeded = count != null && count > LIMIT_COUNT;
        if (exceeded) {
            log.info("Rate limit exceeded for user {} ({} msgs / {}s)", userId, count, WINDOW_SECONDS);
        }

        return exceeded;
    }

}
