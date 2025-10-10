package com.develop.core.security.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginInfo {

    private String userId;

    private String userNickname;

    private String userName;

    private String role;

    public boolean isLoggedIn() {
        return userId != null;
    }
}
