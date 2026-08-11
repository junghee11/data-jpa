package com.develop.websocket.service;

import com.develop.core.exception.ClientException;
import com.develop.core.security.dto.LoginInfo;
import com.develop.core.security.jwt.JwtTokenProvider;
import com.develop.domain.dto.chat.NotificationEvent;
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
import com.develop.websocket.redis.dto.ChatRoomCacheDto;
import com.develop.websocket.redis.publisher.ChatMessagePublisher;
import com.develop.websocket.redis.service.ChatRoomCacheService;
import com.develop.websocket.redis.service.UserPresenceService;
import com.develop.websocket.sqs.producer.NotificationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
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

    private final ChatMessagePublisher chatMessagePublisher;
    private final ChatRoomCacheService chatRoomCacheService;
    private final UserPresenceService userPresenceService;
    private final NotificationProducer notificationProducer;
    private final SimpMessageSendingOperations messagingTemplate;

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

    public ChatMessage sendChatMessage(String sender, String roomId, Message message) {
        ChatMessage chat = ChatMessage.builder()
            .senderId(sender)
            .roomId(roomId)
            .type(MessageType.TALK)
            .content(message.getContent())
            .build();

        chatMessagePublisher.publishMessage(chat);

        Set<String> onlineUsers = getOnlineUsersInRoom(roomId);

        for(String userId : onlineUsers) {
            NotificationEvent event = NotificationEvent.chatMessage(
                userId,
                sender,
                roomId
            );
            notificationProducer.sendNotification(event);
        }

        return chat;
    }

    private Set<String> getOnlineUsersInRoom(String roomId) {
        ChatRoomCacheDto room = chatRoomCacheService.getChatRoom(roomId);
        if (room == null) {
            throw new IllegalArgumentException("존재하지 않는 채팅방입니다");
        }

        Set<String> onlineUser = userPresenceService.getOnlineUsers();

        return Arrays.stream(room.getParticipants()).filter(onlineUser::contains)
            .collect(Collectors.toSet());
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

            chatRoomCacheService.cacheChatRoom(savedChatRoom);
        } else {
            chatRoomRepository.save(room);

            chatRoomCacheService.invalidateRoomCache(roomId);
        }

        ChatMessage chat = ChatMessage.builder()
            .senderId(newUserId)
            .roomId(roomId)
            .type(MessageType.ENTER)
            .content(String.format("%s님이 입장하셨습니다", user.getNickname()))
            .build();
        chatMessageRepository.save(chat);

        chatMessagePublisher.publishJoinMessage(chat);

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

    public ChatRoomCacheDto getOrCreateDirectRoom(String userId, PrivateMessage message) {
        if (message.getReceiverId() == null || message.getReceiverId().isEmpty()) {
            throw new IllegalArgumentException("수신자 정보가 확인되지 않습니다");
        }

        User sender = checkUser(userId);
        User receiver = checkUser(message.getReceiverId());

        String[] participants = {sender.getUserId(), receiver.getUserId()};

        String roomId = generateRoodId(RoomType.DIRECT, participants);

        ChatRoom roomEntity = chatRoomRepository.findById(roomId).orElse(null);
        ChatRoomCacheDto room;

        if (roomEntity == null) {
            ChatRoom newChatRoom = ChatRoom.builder()
                .roomType(RoomType.DIRECT)
                .createdBy(userId)
                .participants(participants)
                .build();
            chatRoomRepository.save(newChatRoom);

            chatRoomCacheService.cacheChatRoom(newChatRoom);

            room = ChatRoomCacheDto.from(newChatRoom);

            messagingTemplate.convertAndSendToUser(receiver.getUserId(), "/queue/chat.room", room);
        } else {
            boolean senderRejoined = roomEntity.rejoinParticipant(sender.getUserId());
            boolean receiverRejoined = roomEntity.rejoinParticipant(receiver.getUserId());

            if (senderRejoined || receiverRejoined) {
                chatRoomRepository.save(roomEntity);
                chatRoomCacheService.cacheChatRoom(roomEntity); 
            }

            room = ChatRoomCacheDto.from(roomEntity);

            if (receiverRejoined) {
                messagingTemplate.convertAndSendToUser(receiver.getUserId(), "/queue/chat.room", room);
            }
        }

        chatRoomCacheService.addUserToRoom(roomId, sender.getUserId());
        chatRoomCacheService.addUserToRoom(roomId, receiver.getUserId());

        return room;
    }

    public void leaveChat(String roomId, String userId) {
        User user;
        try {
            user = checkUser(userId);
        } catch (WebSocketAuthException e) {
            throw new ClientException(e.getMessage());
        }

        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(() ->
            new ClientException("존재하지 않는 채팅방입니다."));

        room.removeParticipant(userId);

        chatRoomCacheService.removeUserFromRoom(roomId, userId);

        if (room.getParticipants().length == 0) {
            chatRoomRepository.delete(room);
            chatRoomCacheService.deleteRoomData(roomId);
            return;
        }

        chatRoomRepository.save(room);

        ChatMessage leaveMessage = ChatMessage.builder()
            .type(MessageType.LEAVE)
            .senderId(userId)
            .roomId(roomId)
            .content(String.format("%s님이 나가셨습니다", user.getNickname()))
            .build();
        ChatMessage savedMessage = chatMessageRepository.save(leaveMessage);

        chatRoomCacheService.cacheMessage(roomId, savedMessage);
        chatRoomCacheService.invalidateRoomCache(roomId);

        messagingTemplate.convertAndSend("/topic/chat/" + roomId, savedMessage);
    }
}
