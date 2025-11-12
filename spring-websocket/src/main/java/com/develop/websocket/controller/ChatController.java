package com.develop.websocket.controller;

import com.develop.core.exception.dto.ErrorMessage;
import com.develop.domain.entity.chat.ChatMessage;
import com.develop.websocket.message.dto.Message;
import com.develop.websocket.message.dto.PrivateMessage;
import com.develop.websocket.message.dto.UserJoinMessage;
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
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable(value = "roomId") String roomId,
                                   @Payload Message message,
                                   Principal principal) {
        return chatService.sendMessage(principal.getName(), roomId, message);
    }

    @MessageMapping("/chat.addUser/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage addUser(@DestinationVariable(value = "roomId") String roomId,
                               @Payload UserJoinMessage message,
                               Principal principal) {
        return chatService.addUser(principal.getName(), message.getUserId(), roomId);
    }

    @MessageMapping("/chat.privateMessage")
    public void sendPrivateMessage(@Payload PrivateMessage message,
                                          Principal principal) {
        chatService.sendPrivateMessage(principal.getName(), message);
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ResponseEntity<ErrorMessage> handleException(Exception e) {
        log.error(e.getMessage());
        return ErrorMessage.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

}
