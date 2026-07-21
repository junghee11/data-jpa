package com.develop.websocket.redis.service;

import com.develop.domain.entity.chat.ChatMessage;
import com.develop.domain.entity.chat.ChatRoom;
import com.develop.domain.repository.chat.ChatRoomRepository;
import com.develop.websocket.redis.dto.ChatMessageCacheDto;
import com.develop.websocket.redis.dto.ChatRoomCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomCacheService {

    private static final String ROOM_INFO_PREFIX = "room:info:";
    private static final String ROOM_MEMBERS_PREFIX = "room:members:";
    private static final String USER_ROOMS_PREFIX = "user:rooms:";
    private static final String ROOM_MESSAGES_PREFIX = "room:messages:";

    private final RedisService redisService;

    private final ChatRoomRepository chatRoomRepository;

    public void cacheChatRoom(ChatRoom chatRoom) {
        String key = ROOM_INFO_PREFIX + chatRoom.getId();
        redisService.save(key, ChatRoomCacheDto.from(chatRoom), 1, TimeUnit.HOURS);
        log.info("Cached chat room: {}", chatRoom.getId());
    }

    public ChatRoomCacheDto getChatRoom(String roomId) {
        String key = ROOM_INFO_PREFIX + roomId;

        ChatRoomCacheDto cached = redisService.get(key, ChatRoomCacheDto.class);
        if (cached != null) {
            log.debug("Cache hit for room: {}", roomId);
            return cached;
        }

        log.debug("Cache miss for room: {}", roomId);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);

        if (chatRoom == null) {
            return null;
        }

        cacheChatRoom(chatRoom);

        return ChatRoomCacheDto.from(chatRoom);
    }

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

    public Set<String> getRoomMembers(String roomId) {
        String key = ROOM_MEMBERS_PREFIX + roomId;
        return redisService.getSet(key);
    }

    public Set<String> getUserRooms(String userId) {
        String key = USER_ROOMS_PREFIX + userId;
        return redisService.getSet(key);
    }

    public boolean isUserInRoom(String roomId, String userId) {
        String key = ROOM_MEMBERS_PREFIX + roomId;
        return redisService.isMember(key, userId);
    }

    public void cacheMessage(String roomId, ChatMessage message) {
        String key = ROOM_MESSAGES_PREFIX + roomId;

        redisService.pushToList(key, ChatMessageCacheDto.from(message));

        Long size = redisService.getListSize(key);
        if (size != null && size > 100) {
            redisService.popFromList(key);
        }

        redisService.expire(key, 24, TimeUnit.HOURS);
    }

    public List<ChatMessageCacheDto> getRecentMessages(String roomId, int count) {
        String key = ROOM_MESSAGES_PREFIX + roomId;
        return redisService.getList(key, -count, -1, ChatMessageCacheDto.class);
    }

    public void incrementUnreadCount(String roomId, String userId) {
        String key = "room:" + roomId + ":unread:" + userId;
        redisService.increment(key);
    }

    public Long getUnreadCount(String roomId, String userId) {
        String key = "room:" + roomId + ":unread:" + userId;
        String count = redisService.get(key);
        return count != null ? Long.parseLong(count) : 0L;
    }

    public void resetUnreadCount(String roomId, String userId) {
        String key = "room:" + roomId + ":unread:" + userId;
        redisService.delete(key);
    }

    public void updateRoomStats(String roomId, String stat, long delta) {
        String key = ROOM_INFO_PREFIX + roomId + ":stats";
        redisService.increment(key + ":" + stat, delta);
    }

    public void invalidateRoomCache(String roomId) {
        String infoKey = ROOM_INFO_PREFIX + roomId;
        redisService.delete(infoKey);
        log.info("Invalidated cache for room :{}", roomId);
    }

}
