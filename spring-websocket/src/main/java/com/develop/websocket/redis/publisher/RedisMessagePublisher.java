package com.develop.websocket.redis.publisher;

import com.develop.domain.entity.chat.ChatMessage;
import com.develop.websocket.exception.WebSocketAuthException;
import com.develop.websocket.message.type.UserStatusUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(String channel, Object message) {
        try {
            log.debug("Publishing message to channel: {}", channel);
            redisTemplate.convertAndSend(channel, message);
        } catch (Exception e) {
            log.error("Error publishing message to channel: {}", channel, e);
            throw new WebSocketAuthException("Failed to publish message");
        }
    }

    public void publishChatMessage(ChatMessage message) {
        String channel = "chat:room:" + message.getRoomId();
        publish(channel, message);
    }

}
