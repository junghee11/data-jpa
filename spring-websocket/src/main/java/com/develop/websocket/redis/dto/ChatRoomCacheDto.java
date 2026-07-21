package com.develop.websocket.redis.dto;

import com.develop.domain.entity.chat.ChatRoom;
import com.develop.domain.entity.chat.ChatType.RoomType;
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
public class ChatRoomCacheDto {

    private String id;

    private String roomName;

    private RoomType roomType;

    private String createdBy;

    private String[] participants;

    private boolean active;

    private LocalDateTime createdAt;

    public static ChatRoomCacheDto from(ChatRoom room) {
        return ChatRoomCacheDto.builder()
            .id(room.getId())
            .roomName(room.getRoomName())
            .roomType(room.getRoomType())
            .createdBy(room.getCreatedBy())
            .participants(room.getParticipants())
            .active(room.isActive())
            .createdAt(room.getCreatedAt())
            .build();
    }

}
