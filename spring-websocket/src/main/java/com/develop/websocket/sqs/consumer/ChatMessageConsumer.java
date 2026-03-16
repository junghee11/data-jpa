package com.develop.websocket.sqs.consumer;

import com.develop.domain.entity.chat.ChatMessage;
import com.develop.domain.repository.chat.ChatMessageRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageConsumer {

    private final ChatMessageRepository messageRepository;

    @SqsListener("${aws.sqs.queues.chat-messages}")
    public void processChatMessage(ChatMessage message) {
        try {
            messageRepository.save(message);
            log.info("Chat message saved: {}", message.getId());
        } catch (Exception e) {
            log.error("Failed to save chat message", e);
            throw e;
        }
    }

}
