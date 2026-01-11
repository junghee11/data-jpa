package com.develop.websocket.listener;

import com.develop.websocket.redis.subscriber.ChatRoomCacheService;
import com.develop.websocket.redis.subscriber.UserPresenceService;
import com.develop.websocket.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;
    private final UserPresenceService userPresenceService;
    private final ChatRoomCacheService chatRoomCacheService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        Principal principal = headerAccessor.getUser();
        if (principal != null) {
            String userId = principal.getName();
            messageTemplate.convertAndSend("/topic/public", String.format("%s님 로그온", userId));

            userPresenceService.setUserOnline(userId, sessionId);

            Set<String> rooms = chatService.getUserRoom(userId);
            for (String roomId : rooms) {
                chatRoomCacheService.addUserToRoom(roomId, userId);
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        Principal principal = headerAccessor.getUser();

        if (principal != null) {
            userPresenceService.setUserOffline(principal.getName());

            messageTemplate.convertAndSend("/topic/public", String.format("%s님 로그오프", principal.getName()));
        }
    }

}
