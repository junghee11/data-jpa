package com.develop.websocket.intercepter.result;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class StompErrorSender {

    private final ObjectProvider<MessageChannel> outboundChannelProvider;

    public StompErrorSender(@Qualifier("clientOutboundChannel") ObjectProvider<MessageChannel> outboundChannelProvider) {
        this.outboundChannelProvider = outboundChannelProvider;
    }

    private static final byte[] EMPTY = new byte[0];

    public void sendError(StompHeaderAccessor accessor, StompCheckResult result) {
        MessageChannel outboundChannel = outboundChannelProvider.getIfAvailable();
        if (outboundChannel == null) {
            return; 
        }

        StompHeaderAccessor errorAccessor = StompHeaderAccessor.create(StompCommand.ERROR);

        errorAccessor.setMessage(result.errorMessage());
        errorAccessor.setSessionId(accessor.getSessionId());
        errorAccessor.setLeaveMutable(true);

        Message<byte[]> message =  MessageBuilder.createMessage(
            EMPTY, errorAccessor.getMessageHeaders()
        );

        outboundChannel.send(message);
    }
}
