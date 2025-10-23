package com.develop.websocket.intercepter;

import com.develop.core.security.jwt.JwtTokenProvider;
import com.develop.websocket.exception.WebSocketAuthException;
import com.develop.websocket.exception.WebSocketBusinessException;
import com.develop.websocket.service.ChatService;
import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompChannelInterceptor implements ChannelInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (isNull(accessor)) {
            throw new WebSocketAuthException("잘못된 요청입니다 (accessor not found)");
        }

        StompCommand command = accessor.getCommand();
        if (command == null) {
            throw new WebSocketAuthException("잘못된 요청입니다 (command not found)");
        }

        switch (command) {
            case CONNECT:
                String token = accessor.getFirstNativeHeader("Authorization");
                if (token == null || !jwtTokenProvider.validateToken(token)) {
                    throw new WebSocketAuthException("로그인 정보가 확인되지 않습니다.");
                }

                String userId = jwtTokenProvider.resolveToken(token).getUserId();

                Principal principal = new UserPrincipal(userId);

                accessor.setUser(principal);

                handleConnect(accessor);
                break;
            case SUBSCRIBE:
                handleSubscribe(accessor);
                break;
            case SEND:
                handleSend(accessor);
                break;
            case DISCONNECT:
                handleDisconnect(accessor);
                break;
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        log.info("{} 님 로그온.. sessionId {}", accessor.getUser(), accessor.getSessionId());
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        if (isNull(accessor.getUser())) {
            throw new WebSocketAuthException("로그인 정보가 확인되지 않습니다");
        } else if (isNull(accessor.getDestination())) {
            throw new WebSocketBusinessException(
                HttpStatus.BAD_REQUEST.toString(),
                "구독 주소가 확인되지 않습니다.."
            );
        }

        log.info("{} 님 {} 구독", accessor.getUser(), accessor.getDestination());
    }

    private void handleSend(StompHeaderAccessor accessor) {
        if (isNull(accessor.getUser())) {
            throw new WebSocketAuthException("로그인 정보가 확인되지 않습니다");
        } else if (isNull(accessor.getDestination())) {
            throw new WebSocketBusinessException(
                HttpStatus.BAD_REQUEST.toString(),
                "발송 주소가 확인되지 않습니다"
            );
        }

        String userId = accessor.getUser().getName();

        if (checkRateLimit(accessor.getDestination().split("/")[2], userId)) {
            throw new WebSocketBusinessException(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "채팅방 도배로 당분간 대화가 금지됩니다.");
        }

    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        log.info("{} 님 로그오프.. sessionId : {}", accessor.getUser(), accessor.getSessionId());

    }

    private boolean checkRateLimit(String destination, String userId) {
        if ("chat.addUser".equals(destination)) {
            return false;
        }
        return chatService.exceedMessageLimit(userId);
    }

}
