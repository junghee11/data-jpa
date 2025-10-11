package com.develop.websocket.controller;

import com.develop.domain.entity.baseball.MatchType;
import com.develop.domain.entity.chat.ChatMessage;
import com.develop.websocket.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private ChatService chatService;

    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable(value = "roomId") String roomId,
                                   @Payload ChatMessage message,
                                   Principal principal) {
        ChatMessage chatMessage = ChatMessage.builder()
                .roomId(roomId).senderId(principal.getName()).build();

        log.info("채팅 메시지 전송: roomId={}, senderId={}", roomId, principal.getName());

        return chatMessage;
    }

    @MessageMapping("/chat.addUser/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage addUser(@DestinationVariable(value = "roomId") String roomId,
                               @Payload ChatMessage message,
                               SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", message.getSenderId());
        headerAccessor.getSessionAttributes().put("roomId", roomId);

        return message;
    }

    @MessageMapping("/chat.privateMessage")
    public void sendPrivateMessage(@Payload ChatMessage message) {
        log.info("message {}", message);
    }

    @MessageMapping("/chat.message/{roomId}")
    public void processMessage(@DestinationVariable String roomId,
                               @Payload ChatMessage message,
                               Principal principal) {
        log.info("roomId {}", roomId);
        log.info("message {}", message);
        log.info("principal {}", principal);
    }
}
