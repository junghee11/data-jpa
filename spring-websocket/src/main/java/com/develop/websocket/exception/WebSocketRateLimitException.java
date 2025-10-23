package com.develop.websocket.exception;

import org.springframework.messaging.MessagingException;

public class WebSocketRateLimitException extends MessagingException {
    public WebSocketRateLimitException(String message) {
        super(message);
    }
}
