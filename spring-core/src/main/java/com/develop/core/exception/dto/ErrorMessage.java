package com.develop.core.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@AllArgsConstructor
public class ErrorMessage {

    private String message;

    public static ResponseEntity<ErrorMessage> of(HttpStatusCode status, String message) {
        return ResponseEntity
            .status(status)
            .body(new ErrorMessage(message));
    }

}
