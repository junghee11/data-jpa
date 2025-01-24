package com.develop.datajpa.response;

import lombok.Builder;
import lombok.Getter;

@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {

    private final T data;
    private final String message;

    @Builder
    public ApiResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponse<T> ok(String message) {
        return ApiResponse.<T>builder()
            .message(message)
            .build();
    }

    public static <T> ApiResponse<T> okWithData(T data) {
        return ApiResponse.<T>builder()
            .data(data)
            .build();
    }

}
