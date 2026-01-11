package com.develop.websocket.redis.subscriber;

import com.develop.domain.entity.chat.ChatMessage;
import com.develop.domain.entity.chat.ChatRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ChatRoomCacheService {

    private static final String ROOM_INFO_PREFIX = "room:info:";
    private static final String ROOM_MEMBERS_PREFIX = "room:members:";
    private static final String USER_ROOMS_PREFIX = "user:rooms:";
    private static final String ROOM_MESSAGES_PREFIX = "room:messages:";

    @Autowired
    private RedisService redisService;

    public void addUserToRoom(String roomId, String userId) {
        String memberKey = ROOM_MEMBERS_PREFIX + roomId;
        redisService.addToSet(memberKey, userId);

        String userRoomsKey = USER_ROOMS_PREFIX + userId;
        redisService.addToSet(userRoomsKey, roomId);

        log.info("Added user {} to room {}", userId, roomId);
    }

    public void removeUserFromRoom(String roomId, String userId) {
        String membersKey = ROOM_MEMBERS_PREFIX + roomId;
        redisService.removeFromSet(membersKey, userId);

        String userRoomsKey = USER_ROOMS_PREFIX + userId;
        redisService.removeFromSet(userRoomsKey, roomId);

        log.info("Removed user {} from room {}", userId, roomId);
    }

    public Set<Object> getRoomMembers(String roomId) {
        String key = ROOM_MEMBERS_PREFIX + roomId;
        return redisService.getSet(key);
    }

    public void cacheMessage(String roomId, ChatMessage message) {
        String key = ROOM_MESSAGES_PREFIX + roomId;

        redisService.pushToList(key, message);

        Long size = redisService.getListSize(key);
        if (size > 100) {
            redisService.popFromList(key);
        }

        redisService.expire(key, 24, TimeUnit.HOURS);
    }

    public void incrementUnreadCount(String roomId, String userId) {
        String key = "room:" + roomId + ":unread:" + userId;
        redisService.increment(key);
    }

    public void updateRoomStats(String roomId, String stat, long delta) {
        String key = ROOM_INFO_PREFIX + roomId + ":stats";
        redisService.increment(key + ":" + stat, delta);
    }

}
