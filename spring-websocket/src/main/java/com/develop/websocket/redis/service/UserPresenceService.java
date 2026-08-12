package com.develop.websocket.redis.service;

import com.develop.websocket.message.type.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPresenceService {

    private static final String ONLINE_USERS_KEY = "users:online";
    private static final String USER_SESSION_PREFIX = "user:session:";
    private static final String USER_STATUS_PREFIX = "user:status:";

    private final RedisService redisService;

    public void setUserOnline(String userId, String sessionId) {
        try {
            redisService.addToSet(ONLINE_USERS_KEY, userId);

            String sessionKey = USER_SESSION_PREFIX + userId;
            redisService.save(sessionKey, sessionId, 30, TimeUnit.MINUTES);

            UserStatus status = UserStatus.builder()
                .userId(userId)
                .status("ONLINE")
                .lastSeenAt(LocalDateTime.now())
                .sessionId(sessionId)
                .build();

            redisService.saveToHash(USER_STATUS_PREFIX + userId, "status", status);
        } catch (Exception e) {
            log.error("Error setting user {} online", userId, e);
        }
    }

    public void setUserOffline(String userId) {
        try {
            redisService.removeFromSet(ONLINE_USERS_KEY, userId);

            String sessionKey = USER_SESSION_PREFIX + userId;
            redisService.delete(sessionKey);

            UserStatus status = UserStatus.builder()
                .userId(userId)
                .status("OFFLINE")
                .lastSeenAt(LocalDateTime.now())
                .build();

            redisService.saveToHash(USER_STATUS_PREFIX + userId, "status", status);

            log.info("User {} is now offline", userId);
        } catch (Exception e) {
            log.error("Error setting user {} offline", userId);
        }
    }

    public boolean isUserOnline(String userId) {
        return redisService.isMember(ONLINE_USERS_KEY, userId);
    }

    public Set<String> getOnlineUsers() {
        return redisService.getSet(ONLINE_USERS_KEY);
    }

    public String getUserSession(String userId) {
        String sessionKey = USER_SESSION_PREFIX + userId;
        return redisService.get(sessionKey);
    }

    public UserStatus getUserStatus(String userId) {
        return redisService.getFromHash(USER_STATUS_PREFIX + userId, "status", UserStatus.class);
    }

    public Map<String, Boolean> getUsersOnlineStatus(List<String> userIds) {
        Map<String, Boolean> statusMap = new HashMap<>();

        for (String userId : userIds) {
            statusMap.put(userId, isUserOnline(userId));
        }

        return statusMap;
    }

    public void refreshUserPresence(String userId) {
        String sessionKey = USER_SESSION_PREFIX + userId;
        if (redisService.hasKey(sessionKey)) {
            redisService.expire(sessionKey, 30, TimeUnit.MINUTES);
        }
    }

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredSessions() {
        Set<String> onlineUsers = getOnlineUsers();

        for (String userId : onlineUsers) {
            String sessionKey = USER_SESSION_PREFIX + userId;

            if (!redisService.hasKey(sessionKey)) {
                log.info("Cleaning up expired session for user {}", userId);
                setUserOffline(userId);
            }
        }
    }

}
