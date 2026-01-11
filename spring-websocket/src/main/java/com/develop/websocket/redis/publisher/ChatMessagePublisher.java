package com.develop.websocket.redis.publisher;

import com.develop.domain.entity.chat.ChatMessage;
import com.develop.websocket.exception.WebSocketBusinessException;
import com.develop.websocket.redis.subscriber.ChatRoomCacheService;
import com.develop.websocket.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessagePublisher {

    private final RedisMessagePublisher redisPublisher;

    private final ChatService chatService;

    private final ChatRoomCacheService chatRoomCacheService;

    public void publishMessage(ChatMessage message) {
        try {
            validateMessage(message);

            ChatMessage processedMessage = chatService.processMessage(message);

            chatRoomCacheService.cacheMessage(message.getRoomId(), processedMessage);

            redisPublisher.publishChatMessage(processedMessage);
        } catch (Exception e) {
            log.error("Error publishing chat message", e);
            throw new WebSocketBusinessException("MESSAGE_PUBLISH_ERROR", e.getMessage());
        }
    }

    private void validateMessage(ChatMessage message) {
        if (message.getRoomId() == null) {
            throw new IllegalArgumentException("채팅방 정보가 확인되지 않습니다");
        } else if (message.getContent() == null || message.getContent().isEmpty()) {
            throw new IllegalArgumentException("메세지 내용이 확인되지 않습니다");
        } else if (message.getContent().length() > 1000) {
            throw new IllegalArgumentException("메세지는 1000자 이하로 작성해주세요");
        }
    }

}
