package com.develop.websocket.redis.publisher;

import com.develop.domain.entity.chat.ChatMessage;
import com.develop.domain.entity.chat.ChatType;
import com.develop.domain.entity.user.User;
import com.develop.domain.repository.chat.ChatMessageRepository;
import com.develop.websocket.exception.WebSocketAuthException;
import com.develop.websocket.exception.WebSocketBusinessException;
import com.develop.websocket.redis.service.ChatRoomCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessagePublisher {

    private final RedisMessagePublisher redisPublisher;

    private final ChatRoomCacheService chatRoomCacheService;

    private final ChatMessageRepository chatMessageRepository;

    public void publishMessage(ChatMessage message) {
        try {
            validateMessage(message);

            ChatMessage processedMessage = processMessage(message);

            chatRoomCacheService.cacheMessage(message.getRoomId(), processedMessage);

            redisPublisher.publishChatMessage(processedMessage);
        } catch (Exception e) {
            log.error("Error publishing chat message", e);
            throw new WebSocketBusinessException("MESSAGE_PUBLISH_ERROR", e.getMessage());
        }
    }

    public void publishJoinMessage(ChatMessage message) {
        chatRoomCacheService.cacheMessage(message.getRoomId(), message);

        redisPublisher.publishRoomEvents(message);
    }

    public void publishLeaveMessage(String roomId, User user) {
        if(!chatRoomCacheService.isUserInRoom(roomId, user.getUserId())) {
            throw new WebSocketAuthException("채팅방 참여자가 아닙니다");
        }

        ChatMessage leaveMessage = ChatMessage.builder()
            .type(ChatType.MessageType.LEAVE)
            .senderId(user.getUserId())
            .roomId(roomId)
            .content(String.format("%s님이 나가셨습니다", user.getNickname()))
            .build();
        ChatMessage savedMessage = chatMessageRepository.save(leaveMessage);

        chatRoomCacheService.cacheMessage(roomId, savedMessage);

        redisPublisher.publishRoomEvents(leaveMessage);
    }

    private void validateMessage(ChatMessage message) {
        if (message.getRoomId() == null) {
            throw new IllegalArgumentException("채팅방 정보가 확인되지 않습니다");
        } else if (message.getContent() == null || message.getContent().isEmpty()) {
            throw new IllegalArgumentException("메세지 내용이 확인되지 않습니다");
        } else if (message.getContent().length() > 1000) {
            throw new IllegalArgumentException("메세지는 1000자 이하로 작성해주세요");
        }
    }

    private ChatMessage processMessage(ChatMessage message) {
        String filteredContent = filterProfanity(message.getContent());
        message.setContent(filteredContent);

        ChatMessage savedMessage = chatMessageRepository.save(message);

        return savedMessage;
    }

    private String filterProfanity(String content) {
        String[] bannedWords = {"비속어", "욕", "나쁜말"};

        for (String word : bannedWords) {
            content = content.replaceAll("(?i)" + Pattern.quote(word), "***");
        }

        return content;
    }

}
