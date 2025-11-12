package com.develop.websocket.message.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class PrivateMessage {

    private String receiverId;

}
