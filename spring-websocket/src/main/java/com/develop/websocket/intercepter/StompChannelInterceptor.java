package com.develop.websocket.intercepter;

import com.develop.core.security.jwt.JwtTokenProvider;
import com.develop.websocket.intercepter.result.StompCheckResult;
import com.develop.websocket.intercepter.result.StompErrorSender;
import com.develop.websocket.redis.service.RedisService;
import com.develop.websocket.redis.service.UserPresenceService;
import com.develop.websocket.service.ChatService;
import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatService chatService;
    private final RedisService redisService;
    private final UserPresenceService userPresenceService;
    private final StompErrorSender stompErrorSender;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        StompCheckResult result = StompCheckResult.ok();

        StompCommand command = accessor.getCommand();
        if (command == null) {
            result = StompCheckResult.invalidMessage("잘못된 연결 요청입니다");
            stompErrorSender.sendError(accessor, result);
            
            return null;
        }

        switch (command) {
            case CONNECT -> result = handleConnect(accessor);
            case SUBSCRIBE -> result = handleSubscribe(accessor);
            case SEND -> result = handleSend(accessor);
            case DISCONNECT -> result = handleDisconnect(accessor);
        }

        if (!result.success()) {
            stompErrorSender.sendError(accessor, result);

            return null;
        }

        return message;
    }

    private StompCheckResult handleConnect(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("Authorization");
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return StompCheckResult.unauthorized();
        }

        String userId = jwtTokenProvider.resolveToken(token).getUserId();

        Principal principal = new UserPrincipal(userId);

        accessor.setUser(principal);

        if (!checkConcurrentConnectionLimit(accessor.getUser().getName())) {
            log.info("{}님은 이미 접속중인 계정입니다", accessor.getUser().getName());

            return StompCheckResult.error("ALREADY_CONNECTED", "이미 접속중인 계정입니다");
        }

        log.info("{} 님 로그온", accessor.getUser());

        return StompCheckResult.ok();
    }

    private StompCheckResult handleSubscribe(StompHeaderAccessor accessor) {
        if (isNull(accessor.getUser())) {
            return StompCheckResult.unauthorized();
        } else if (isNull(accessor.getDestination())) {
            return StompCheckResult.invalidMessage("구독 주소가 확인되지 않습니다");
        }

        log.info("{} 님 {} 구독", accessor.getUser(), accessor.getDestination());

        return StompCheckResult.ok();
    }

    private StompCheckResult handleSend(StompHeaderAccessor accessor) {
        if (isNull(accessor.getUser())) {
            return StompCheckResult.unauthorized();
        } else if (isNull(accessor.getDestination())) {
            return StompCheckResult.invalidMessage("발송 주소가 확인되지 않습니다");
        }

        String userId = accessor.getUser().getName();

        updateUserOnlineStatus(userId, true);

        if (checkRateLimit(accessor.getDestination().split("/")[2], userId)) {
            return StompCheckResult.invalidMessage("채팅방 도배로 당분간 대화가 금지됩니다.");
        }

        return StompCheckResult.ok();
    }

    private StompCheckResult handleDisconnect(StompHeaderAccessor accessor) {
        log.info("{} 님 로그오프.. sessionId : {}", accessor.getUser(), accessor.getSessionId());

        return StompCheckResult.ok();
    }

    private boolean checkRateLimit(String destination, String userId) {
        if ("chat.addUser".equals(destination)) {
            return false;
        }
        return chatService.exceedMessageLimit(userId);
    }

    private boolean checkConcurrentConnectionLimit (String userId) {
        String userSession = userPresenceService.getUserSession(userId);
        if (userSession != null) {
            log.info("유저 접속 중.. {}", userSession);
        }

        return userSession == null;
    }

    private void updateUserOnlineStatus (String userId, boolean online) {
        if (online) {
            userPresenceService.refreshUserPresence(userId);
        } else {
            redisService.delete("user:session:" + userId);
        }
    }

}
