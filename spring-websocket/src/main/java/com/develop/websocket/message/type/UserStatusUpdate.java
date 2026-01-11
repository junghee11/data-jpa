package com.develop.websocket.message.type;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatusUpdate {

    private String userId;

    private String status;

}
