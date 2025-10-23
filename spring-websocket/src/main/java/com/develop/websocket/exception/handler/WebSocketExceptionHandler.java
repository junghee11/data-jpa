package com.develop.websocket.exception.handler;

import com.develop.core.exception.dto.ErrorMessage;
import com.develop.websocket.exception.WebSocketAuthException;
import com.develop.websocket.exception.WebSocketBusinessException;
import com.develop.websocket.exception.WebSocketRateLimitException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class WebSocketExceptionHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageExceptionHandler(Exception.class)
    public void handleGlobalException(Exception e, Principal principal) {
        log.error("Global WebSocket exception", e);

        if (principal != null) {
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                ErrorMessage.of(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage()
                )
            );
        }
    }

    @MessageExceptionHandler(WebSocketAuthException.class)
    public void handleAuthException(WebSocketAuthException e) {
        log.error("Authentication error {}", e.getMessage());
    }

    @MessageExceptionHandler(WebSocketBusinessException.class)
    public void handleBusinessException(WebSocketBusinessException e, Principal principal) {
        log.error("BusinessException error {}", e.getMessage());

        if (principal != null) {
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                ErrorMessage.of(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
                )
            );
        }
    }

    @MessageExceptionHandler(WebSocketRateLimitException.class)
    public void handleRateLimitException(WebSocketRateLimitException e, Principal principal) {
        log.warn("Rate limit exceeded for user : {}", principal != null ? principal.getName() : "unknown");

        if (principal != null) {
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                ErrorMessage.of(
                    HttpStatus.BANDWIDTH_LIMIT_EXCEEDED,
                    e.getMessage()
                )
            );
        }
    }

}
