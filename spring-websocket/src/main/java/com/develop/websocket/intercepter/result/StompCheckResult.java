package com.develop.websocket.intercepter.result;

public record StompCheckResult(
    boolean success,
    String errorCode,
    String errorMessage
) {

    public static StompCheckResult ok() {
        return new StompCheckResult(true, null, null);
    }

    public static StompCheckResult error(String code, String message) {
        return new StompCheckResult(false, code, message);
    }

    public static StompCheckResult unauthorized() {
        return new StompCheckResult(false, "UNAUTHORIZED", "로그인 정보가 확인되지 않습니다");
    }

    public static StompCheckResult invalidMessage(String message) {
        return new StompCheckResult(false, "INVALID_MESSAGE", message);
    }

}
