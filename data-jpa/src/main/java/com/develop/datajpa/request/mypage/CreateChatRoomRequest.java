package com.develop.datajpa.request.mypage;

import com.develop.domain.entity.chat.ChatType.RoomType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CreateChatRoomRequest {

    @NotNull(message = "채팅방 유형을 확인해주세요")
    private RoomType roomType;

    private String roomName;

    @NotEmpty(message = "대화 상대를 선택해주세요")
    private List<String> participants;

}
