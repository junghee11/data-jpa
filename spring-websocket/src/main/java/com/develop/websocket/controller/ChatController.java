package com.develop.websocket.controller;

import com.develop.core.exception.dto.ErrorMessage;
import com.develop.domain.entity.chat.ChatMessage;
import com.develop.websocket.exception.WebSocketAuthException;
import com.develop.websocket.message.dto.Message;
import com.develop.websocket.message.dto.PrivateMessage;
import com.develop.websocket.message.dto.UserJoinMessage;
import com.develop.websocket.redis.subscriber.ChatRoomCacheService;
import com.develop.websocket.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomCacheService chatRoomCacheService;

    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable(value = "roomId") String roomId,
                                   @Payload Message message,
                                   Principal principal) {
        String userId = principal.getName();

        if(!chatRoomCacheService.isUserInRoom(roomId, userId)) {
            throw new WebSocketAuthException("채팅방 참여자가 아닙니다");
        }

        ChatMessage chatMessage = chatService.sendChatMessage(userId, roomId, message);

        return chatMessage;
    }

    @MessageMapping("/chat.addUser/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage addUser(@DestinationVariable(value = "roomId") String roomId,
                               @Payload UserJoinMessage message,
                               Principal principal) {
        ChatMessage chatMessage = chatService.addUser(principal.getName(), message.getUserId(), roomId);

        return chatMessage;
    }

    @MessageMapping("/chat.privateMessage")
    public void sendPrivateMessage(@Payload PrivateMessage message,
                                   Principal principal) {
        chatService.sendPrivateMessage(principal.getName(), message);
    }

    @MessageMapping("/chat.leaveRoom/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public void leaveRoom(@DestinationVariable(value = "roomId") String roomId,
                          Principal principal) {
        chatService.leaveChat(roomId, principal.getName());
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ResponseEntity<ErrorMessage> handleException(Exception e) {
        log.error(e.getMessage());
        return ErrorMessage.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

}
