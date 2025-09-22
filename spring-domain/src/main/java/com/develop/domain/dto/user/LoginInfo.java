package com.develop.domain.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginInfo {

    private String userId;

    public boolean isLoggedIn() {
        return userId != null;
    }
}
