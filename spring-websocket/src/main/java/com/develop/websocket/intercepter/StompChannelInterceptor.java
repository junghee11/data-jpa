package com.develop.websocket.intercepter;

import com.develop.websocket.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.Duration;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompChannelInterceptor implements ChannelInterceptor {

    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("StompChannelInterceptor presend =========================");

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        log.info("token {}", accessor.getNativeHeader("Authorization"));

        if (nonNull(accessor)) {
            StompCommand command = accessor.getCommand();

            switch (command) {
                case CONNECT :
                    handleConnect(accessor);
                    break;
                case SUBSCRIBE:
                    handleSubscribe(accessor);
                    break;
                case SEND:
                    handleSend(accessor);
                    break;
                case DISCONNECT:
                    handleDisconnect(accessor);
                    break;
            }
        }
        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        log.info("handleConnect =========================================");
        log.info("user = {}", accessor.getUser());
        log.info("destination = {}", accessor.getDestination());
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        log.info("handleSubscribe =========================================");
        log.info("user = {}", accessor.getUser());
        log.info("destination = {}", accessor.getDestination());
    }

    private void handleSend(StompHeaderAccessor accessor) {
        log.info("handleSend =========================================");

        log.info("user = {}", accessor.getUser());
        log.info("destination = {}", accessor.getDestination());

        if (isNull(accessor.getUser())) {
            log.info("user 정보 없음..");
            return;
        } else if (isNull(accessor.getDestination())) {
            log.info("destination 정보 없음..");
            return;
        }

        String userId = accessor.getUser().getName();
        String destination = accessor.getDestination();

        String roomId = extractRoomIdFromAppDestination(destination);

        hasRoomAccess(userId, roomId);
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        log.info("handleDisconnect =========================================");
        log.info("accessor : {}", accessor);
        Principal user = accessor.getUser();

        log.info("user : {}", user);

    }

    private boolean hasRoomAccess(String userId, String roomId) {
        return chatService.isUserInRoom(userId, roomId);
    }

    private String extractRoomIdFromAppDestination(String destination) {
        log.info("room id from app destination : {}", destination);

        return null;
    }

    private String extractUserIdFromDestination(String destination) {
        log.info("user id from destination : {}", destination);
        return null;
    }

}
