package com.develop.websocket.redis.service;

import com.develop.domain.entity.chat.ChatMessage;
import com.develop.websocket.message.type.UserStatusUpdate;
import com.develop.websocket.redis.subscriber.ChatRoomCacheService;
import com.develop.websocket.redis.subscriber.UserPresenceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final ChatRoomCacheService chatRoomCacheService;
    private final UserPresenceService userPresenceService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel());

            String messageBody = new String(message.getBody());

            log.debug("Received message from channel: {} - {}", channel, messageBody);

            if (channel.startsWith("chat:room")) {
                ChatMessage chatMessage = objectMapper.readValue(messageBody, ChatMessage.class);

                if (channel.contains(":messages")) {
                    handleChatMessage(chatMessage);
                } else if (channel.contains(":join")) {
                    handleJoinMessage(chatMessage);
                } else if (channel.contains(":leave")) {
                    handleLeaveMessage(chatMessage);
                }
            } else if (channel.equals("user:status")) {
                handleUserStatus(messageBody);
            }

        } catch (Exception e) {
            log.error("Error processing Redis message", e);
        }
    }

    private void handleChatMessage(ChatMessage chatMessage) {
        String roomId = chatMessage.getRoomId();

        Set<Object> members = chatRoomCacheService.getRoomMembers(roomId);

        for (Object memberObj : members) {
            String memberId = (String) memberObj;

            if (userPresenceService.isUserOnline(memberId)) {
                sendToUser(memberId, chatMessage);
            } else {
                chatRoomCacheService.incrementUnreadCount(roomId, memberId);
            }
        }

        chatRoomCacheService.updateRoomStats(roomId, "messageCount", 1);

        messagingTemplate.convertAndSend("/topic/room/" + roomId, chatMessage);

        log.debug("Broadcast chat message to room : {}", roomId);
    }

    private void handleUserStatus(String messageBody) {
        try {
            UserStatusUpdate statusUpdate = objectMapper.readValue(messageBody, UserStatusUpdate.class);

            messagingTemplate.convertAndSend("/topic/users/status", statusUpdate);

            log.debug("Broadcast user status: userId={}, status={}", statusUpdate.getUserId(), statusUpdate.getStatus());

        } catch (Exception e) {
            log.error("Error handling user status", e);
        }
    }

    private void handleJoinMessage(ChatMessage message) {
        String roomId = message.getRoomId();
        String userId = message.getSenderId();

        chatRoomCacheService.addUserToRoom(roomId, userId);

        messagingTemplate.convertAndSend("/topic/room/" + roomId, message);

        chatRoomCacheService.updateRoomStats(roomId, "memberCount", 1);

        log.info("User {} joined room {}", userId, roomId);
    }

    private void handleLeaveMessage(ChatMessage message) {
        String roomId = message.getRoomId();
        String userId = message.getSenderId();

        chatRoomCacheService.removeUserFromRoom(roomId, userId);

        messagingTemplate.convertAndSend("/topic/room/" + roomId, message);

        chatRoomCacheService.updateRoomStats(roomId, "memberCount", -1);

        log.info("User {} left room {}", userId, roomId);
    }

    private void sendToUser(String userId, ChatMessage message) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/messages",
            message
        );
    }
}
