package com.develop.datajpa.request.valid;

import com.develop.datajpa.response.ClientException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class InvalidRequest {
    private static final String[] invalidKeywords = {"/","(", ")", "$", "+", "=", "select", "|", "[", "]", "sleep", "_", ";", "*"};

    public static void containsInvalidKeyword(String... args) {
        String request = String.join( "", args);
        for (String invalidKeyword : invalidKeywords) {
            if (request.contains(invalidKeyword)) {
                log.error("sql injection 시도 감지, request = {}", request);
                throw new ClientException("잘못된 요청값입니다.");
            }
        }
    }
}
