package com.develop.websocket.service;

import com.develop.core.security.dto.LoginInfo;
import com.develop.core.security.jwt.JwtTokenProvider;
import com.develop.domain.dto.user.UserDto;
import com.develop.domain.entity.chat.ChatMessage;
import com.develop.domain.entity.chat.ChatRoom;
import com.develop.domain.entity.chat.ChatType.MessageType;
import com.develop.domain.entity.chat.ChatType.RoomType;
import com.develop.domain.entity.user.User;
import com.develop.domain.entity.user.UserType;
import com.develop.domain.repository.chat.ChatMessageRepository;
import com.develop.domain.repository.chat.ChatRoomRepository;
import com.develop.domain.repository.user.UserRepository;
import com.develop.websocket.exception.WebSocketAuthException;
import com.develop.websocket.exception.WebSocketBusinessException;
import com.develop.websocket.message.dto.Message;
import com.develop.websocket.message.dto.PrivateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.develop.domain.entity.chat.ChatRoom.generateRoodId;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final long MESSAGE_LIMIT_SECOND = 10;
    private final long MESSAGE_LIMIT_COUNT = 20L;

    public UserDto getUserInfo(String token) {
        LoginInfo loginInfo = jwtTokenProvider.resolveToken(token);

        UserDto user = userRepository.findByUserId(loginInfo.getUserId());

        return user;
    }

    public ChatRoom getChatRoom(String roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(() ->
            new WebSocketBusinessException(HttpStatus.BAD_REQUEST.getReasonPhrase(), "존재하지 않는 채팅방입니다."));

        return room;
    }

    public boolean isUserInRoom(String userId, String roomId) {
        User user = checkUser(userId);

        ChatRoom room = getChatRoom(roomId);
        if (RoomType.DIRECT == room.getRoomType()) {
            throw new WebSocketBusinessException(HttpStatus.BAD_REQUEST.getReasonPhrase(), "잘못된 요청입니다");
        }

        return Arrays.asList(room.getParticipants()).contains(user.getUserId());
    }

    public ChatMessage sendMessage(String sender, String roomId, Message message) {
        validateMessage(message);

        boolean isUserInRoom = isUserInRoom(sender, roomId);
        if (!isUserInRoom) {
            throw new WebSocketBusinessException(HttpStatus.FORBIDDEN.getReasonPhrase(), "채팅방 참여자가 아닙니다");
        }

        ChatMessage chat = ChatMessage.builder()
            .senderId(sender)
            .roomId(roomId)
            .type(MessageType.TALK)
            .content(message.getContent())
            .build();
        chatMessageRepository.save(chat);

        return chat;
    }

    public ChatMessage addUser(String userId, String newUserId, String roomId) {
        if (newUserId == null || newUserId.isEmpty()) {
            throw new IllegalArgumentException("초대하실 회원을 선택해주세요");
        }

        User user = checkUser(newUserId);

        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(() ->
            new WebSocketBusinessException(HttpStatus.BAD_REQUEST.getReasonPhrase(), "존재하지 않는 채팅방입니다."));

        room.addParticipant(newUserId);

        if (RoomType.DIRECT == room.getRoomType()) {
            String[] participants = room.getParticipants();
            ChatRoom newRoom = ChatRoom.builder()
                .roomType(RoomType.GROUP)
                .participants(participants)
                .createdBy(userId)
                .build();

            ChatRoom savedChatRoom = chatRoomRepository.save(newRoom);
            roomId = savedChatRoom.getId();
        } else {
            chatRoomRepository.save(room);
        }

        ChatMessage chat = ChatMessage.builder()
            .senderId(newUserId)
            .roomId(roomId)
            .type(MessageType.ENTER)
            .content(String.format("%s님이 입장하셨습니다", user.getNickname()))
            .build();
        chatMessageRepository.save(chat);

        return chat;
    }

    public User checkUser(String userId) {
        User user = userRepository.findOptionalByUserId(userId).orElseThrow(() ->
            new WebSocketAuthException("가입정보가 확인되지 않습니다."));
        if (UserType.Role.DORMANT.name().equals(user.getRole())) {
            throw new WebSocketAuthException("휴면회원 입니다. 휴면해제 후 로그인 해주세요.");
        } else if (UserType.Role.WITHDRAWAL.name().equals(user.getRole())) {
            throw new WebSocketAuthException("탈퇴처리된 회원입니다.");
        }

        return user;
    }

    public ChatMessage sendPrivateMessage(String userId, PrivateMessage message) {
        validatePrivateMessage(message);

        User sender = checkUser(userId);
        User receiver = checkUser(message.getReceiverId());

        String[] participants = {sender.getUserId(), receiver.getUserId()};

        String roomId = generateRoodId(RoomType.DIRECT, participants);
        ;

        Optional<ChatRoom> room = chatRoomRepository.findById(roomId);

        if (room.isEmpty()) {
            ChatRoom newChatRoom = ChatRoom.builder()
                .roomType(RoomType.DIRECT)
                .createdBy(userId)
                .participants(participants)
                .build();
            chatRoomRepository.save(newChatRoom);
        }

        ChatMessage chat = ChatMessage.builder()
            .senderId(userId)
            .receiverId(message.getReceiverId())
            .roomId(roomId)
            .type(MessageType.TALK)
            .content(message.getContent())
            .build();
        chatMessageRepository.save(chat);

        return chat;
    }

    public void validateMessage(Message message) {
        if (message.getContent() == null || message.getContent().isEmpty()) {
            throw new IllegalArgumentException("메세지 내용을 입력해주세요");
        }

        if (message.getContent().length() > 1000) {
            throw new IllegalArgumentException("메세지 내용은 1000자 이하로 작성해주세요");
        }
    }

    public void validatePrivateMessage(PrivateMessage message) {
        if (message.getReceiverId() == null || message.getReceiverId().isEmpty()) {
            throw new IllegalArgumentException("수신자 정보가 확인되지 않습니다");
        }

        if (message.getContent() == null || message.getContent().isEmpty()) {
            throw new IllegalArgumentException("메세지 내용을 입력해주세요");
        }

        if (message.getContent().length() > 1000) {
            throw new IllegalArgumentException("메세지 내용은 1000자 이하로 작성해주세요");
        }
    }

    public void leaveChat(String userId) {
        User user = checkUser(userId);

        List<ChatRoom> roomList = chatRoomRepository.findByParticipantAndRoomTypeNot(userId, RoomType.DIRECT.name());

        if (!roomList.isEmpty()) {
            List<ChatMessage> leaveMessageList = new ArrayList<>();

            roomList.forEach(room -> {
                room.removeParticipant(userId);

                ChatMessage chatMessage = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .senderId(user.getUserId())
                    .roomId(room.getId())
                    .content(String.format("%s님이 나가셨습니다", user.getNickname()))
                    .build();

                leaveMessageList.add(chatMessage);
            });

            chatRoomRepository.saveAll(roomList);
            chatMessageRepository.saveAll(leaveMessageList);
        }
    }

    public boolean exceedMessageLimit(String userId) {
        long messageCount = chatMessageRepository.countBySenderIdAndCreatedAtAfter
            (userId, LocalDateTime.now().minusSeconds(MESSAGE_LIMIT_SECOND));

        return messageCount > MESSAGE_LIMIT_COUNT;
    }
}
