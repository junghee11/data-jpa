package com.develop.websocket.sqs.producer;

import com.develop.domain.entity.chat.ChatMessage;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatEventProducer {

    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.queues.chat-messages}")
    private String chatMessageQueue;

    public void sendChatMessage(ChatMessage message) {
        sqsTemplate.send(to -> to
            .queue(chatMessageQueue)
            .payload(message));
    }

}
