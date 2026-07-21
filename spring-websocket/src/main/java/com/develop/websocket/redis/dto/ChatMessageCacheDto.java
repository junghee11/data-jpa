package com.develop.websocket.redis.dto;

import com.develop.domain.entity.chat.ChatMessage;
import com.develop.domain.entity.chat.ChatType.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageCacheDto {

    private String id;

    private String roomId;

    private String senderId;

    private String receiverId;

    private MessageType type;

    private String content;

    private LocalDateTime createdAt;

    public static ChatMessageCacheDto from(ChatMessage message) {
        return ChatMessageCacheDto.builder()
            .id(message.getId())
            .roomId(message.getRoomId())
            .senderId(message.getSenderId())
            .receiverId(message.getReceiverId())
            .type(message.getType())
            .content(message.getContent())
            .createdAt(message.getCreatedAt())
            .build();
    }

}
