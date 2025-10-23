package com.develop.websocket.exception;

import org.springframework.messaging.MessagingException;

public class WebSocketBusinessException extends MessagingException {
    private final String errorCode;

    public WebSocketBusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
