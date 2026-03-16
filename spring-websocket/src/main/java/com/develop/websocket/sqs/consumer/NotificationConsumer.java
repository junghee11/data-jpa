package com.develop.websocket.sqs.consumer;

import com.develop.domain.dto.chat.NotificationEvent;
import com.develop.domain.entity.chat.Notification;
import com.develop.domain.repository.chat.NotificationRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;

    @SqsListener("${aws.sqs.queues.notifications}")
    public void processNotification(NotificationEvent event) {
        try {
            notificationRepository.save(Notification.from(event));

            log.info("Notification sent to user: {}", event.userId());
        } catch (Exception e) {
            log.error("Failed to send notification", e);
            throw e;
        }
    }

}
