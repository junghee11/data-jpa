package com.develop.websocket.message.type;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserStatus {

    String userId;

    String status;

    LocalDateTime lastSeenAt;

    String sessionId;

    @Builder
    public UserStatus(String userId, String status, LocalDateTime lastSeenAt, String sessionId) {
        this.userId = userId;
        this.status = status;
        this.lastSeenAt = lastSeenAt;
        this.sessionId = sessionId;
    }
}
