package com.develop.websocket.listener;

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

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        log.info("Received a new web socket connection. sessionId : {}", sessionId);

        Principal principal = headerAccessor.getUser();
        if (principal != null) {
            messageTemplate.convertAndSend("/topic/public", String.format("%s님 로그온", principal.getName()));
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        log.info("sessionId : " + sessionId);

        Principal principal = headerAccessor.getUser();

        if (principal != null) {
            chatService.leaveChat(principal.getName());

            messageTemplate.convertAndSend("/topic/public", String.format("%s님 로그오프", principal.getName()));
        }
    }

}
