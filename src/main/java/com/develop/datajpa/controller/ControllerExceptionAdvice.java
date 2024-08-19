package com.develop.datajpa.controller;


import com.develop.datajpa.response.ClientException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Map<String, Object>> HttpClientErrorException(HttpClientErrorException e) {
        return new ResponseEntity<>(
            Map.of("message", e.getStatusText()),
            e.getStatusCode()
        );
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ClassNotFoundException.class)
    public ResponseEntity<Map<String, Object>> ClassNotFoundException(ClassNotFoundException e) {
        return new ResponseEntity<>(
            Map.of("message", e.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> MethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        for (int i = 0; i < e.getErrorCount(); i++) {
            log.info("errorMessage{} : {}", i + 1, e.getBindingResult().getFieldErrors().get(i).getDefaultMessage());
        }

        return new ResponseEntity<>(
            Map.of("message", isNull(message) ? "잘못된 요청값입니다" : message),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String errorMessage = "필수 입력값이 누락되었습니다.";  // TODO : 배포 후에는 이 메세지로 변경 필요
        return new ResponseEntity<>(
            Map.of("message", e.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = "필수 입력값이 누락되었습니다.";  // TODO : 배포 후에는 이 메세지로 변경 필요
        return new ResponseEntity<>(
            Map.of("message", e.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ClientException.class)
    public ResponseEntity<Map<String, Object>> exception(Exception e) {
        return new ResponseEntity<>(
            Map.of("message", e.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

}
