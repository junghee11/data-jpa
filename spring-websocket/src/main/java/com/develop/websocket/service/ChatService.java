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
import java.util.Set;
import java.util.stream.Collectors;

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

    public Set<String> getUserRoom(String userId) {
        List<ChatRoom> roomList = chatRoomRepository.findByParticipant(userId);

        return roomList.stream().map(ChatRoom::getId).collect(Collectors.toSet());
    }

    public ChatMessage processMessage(ChatMessage message) {
        String filteredContent = filterProfanity(message.getContent());
        message.setContent(filteredContent);

        ChatMessage savedMessage = chatMessageRepository.save(message);

        return savedMessage;
    }

    private String filterProfanity(String content) {
        String[] bannedWords = {"비속어", "욕", "나쁜말"};

        for (String word : bannedWords) {
            content = content.replace("(?i)" + word, "***");
        }

        return content;
    }

    public ChatMessage createChatMessage(String sender, String roomId, Message message) {
        ChatMessage chat = ChatMessage.builder()
            .senderId(sender)
            .roomId(roomId)
            .type(MessageType.TALK)
            .content(message.getContent())
            .build();

        return chat;
    }

    public ChatMessage addUser(String hostId, String newUserId, String roomId) {
        if (newUserId == null || newUserId.isEmpty()) {
            throw new IllegalArgumentException("초대하실 회원을 선택해주세요");
        }

        User user = checkUser(newUserId);

        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(() ->
            new WebSocketBusinessException(HttpStatus.BAD_REQUEST.getReasonPhrase(), "존재하지 않는 채팅방입니다."));

        room.addParticipant(hostId, newUserId);

        if (RoomType.DIRECT == room.getRoomType()) {
            String[] participants = room.getParticipants();
            ChatRoom newRoom = ChatRoom.builder()
                .roomType(RoomType.GROUP)
                .participants(participants)
                .createdBy(hostId)
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

    public void sendPrivateMessage(String userId, PrivateMessage message) {
        if (message.getReceiverId() == null || message.getReceiverId().isEmpty()) {
            throw new IllegalArgumentException("수신자 정보가 확인되지 않습니다");
        }

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
