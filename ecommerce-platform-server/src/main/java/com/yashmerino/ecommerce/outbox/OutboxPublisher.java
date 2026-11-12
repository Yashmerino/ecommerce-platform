package com.yashmerino.ecommerce.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;
import com.yashmerino.ecommerce.kafka.events.PaymentRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.outbox.publisher.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:1000}")
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxEventRepository
                .findTop100ByPublishedAtIsNullAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(LocalDateTime.now());
        for (OutboxEvent event : events) {
            publish(event);
        }
    }

    private void publish(OutboxEvent event) {
        try {
            Object payload = deserializePayload(event);
            kafkaTemplate.send(event.getTopic(), event.getMessageKey(), payload).get(10, TimeUnit.SECONDS);
            event.markPublished();
        } catch (Exception exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            event.markFailed(exception);
            log.error("Outbox event {} could not be published; retry scheduled", event.getEventId(), exception);
        }
        outboxEventRepository.save(event);
    }

    private Object deserializePayload(OutboxEvent event) throws Exception {
        if (PaymentRequestedEvent.class.getName().equals(event.getEventType())) {
            return objectMapper.readValue(event.getPayload(), PaymentRequestedEvent.class);
        }
        if (NotificationRequestedEvent.class.getName().equals(event.getEventType())) {
            return objectMapper.readValue(event.getPayload(), NotificationRequestedEvent.class);
        }
        throw new IllegalArgumentException("Unsupported outbox event type: " + event.getEventType());
    }
}