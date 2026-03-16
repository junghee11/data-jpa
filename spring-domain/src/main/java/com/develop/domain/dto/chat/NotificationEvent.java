package com.develop.domain.dto.chat;

import com.develop.domain.entity.chat.NotificationType;

import java.time.Instant;

public record NotificationEvent(
    String userId,
    String message,
    NotificationType type,
    String senderId,
    String roomId,
    String targetUrl,
    Instant timestamp) {

    public static NotificationEvent chatMessage(String userId, String senderName, String roomId) {
        return new NotificationEvent(userId, senderName + "님이 메세지를 보냈습니다", NotificationType.CHAT_MESSAGE,
            null, roomId, "/chat/room/" + roomId, Instant.now());
    }

    public static NotificationEvent mention(String userId, String senderName, String roomId) {
        return new NotificationEvent(userId, senderName + "님이 회원님을 언급했습니다", NotificationType.MENTION,
            null, roomId, "/chat/room/" + roomId, Instant.now());
    }

}
