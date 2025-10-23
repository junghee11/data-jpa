package com.develop.websocket.exception;

import org.springframework.messaging.MessagingException;

public class WebSocketAuthException extends MessagingException {
    public WebSocketAuthException(String message) {
        super(message);
    }

    public WebSocketAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
