package com.develop.websocket.sqs.producer;

import com.develop.domain.dto.chat.NotificationEvent;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.queues.notifications}")
    private String notificationQueue;

    public void sendNotification(NotificationEvent event) {
        sqsTemplate.send(to -> to
            .queue(notificationQueue)
            .payload(event)
        );

        log.info("Notification event sent to SQS: {}", event);
    }

}
