package com.develop.websocket.service;

import com.develop.core.exception.ClientException;
import com.develop.domain.entity.chat.ChatRoom;
import com.develop.domain.entity.user.User;
import com.develop.domain.entity.user.UserType;
import com.develop.domain.repository.chat.ChatRoomRepository;
import com.develop.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    public boolean isUserInRoom(String userId, String roomId) {
        Optional<User> user = userRepository.findOptionalByUserId(userId);
        if (user.isEmpty()) {
            throw new ClientException("가입정보가 확인되지 않습니다.");
        } else if (UserType.Role.DORMANT.name().equals(user.get().getRole())) {
            throw new ClientException("휴면회원 입니다. 휴면해제 후 로그인 해주세요.");
        } else if (UserType.Role.WITHDRAWAL.name().equals(user.get().getRole())) {
            throw new ClientException("탈퇴처리된 회원입니다.");
        }

        user.get();
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(() ->
                new ClientException("존재하지 않는 채팅방입니다"));

        return room.getParticipants().contains(user.get());
    }
}
