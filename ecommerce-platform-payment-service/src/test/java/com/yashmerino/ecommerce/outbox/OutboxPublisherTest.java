package com.yashmerino.ecommerce.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashmerino.ecommerce.kafka.events.PaymentResultEvent;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private OutboxPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new OutboxPublisher(outboxEventRepository, new ObjectMapper(), kafkaTemplate);
    }

    @Test
    void publishesAndMarksOutboxEvent() {
        OutboxEvent event = createEvent();
        when(outboxEventRepository.findTop100ByPublishedAtIsNullAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(any()))
                .thenReturn(List.of(event));
        when(kafkaTemplate.send(eq("payment.result"), eq("100"), any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        publisher.publishPendingEvents();

        assertNotNull(event.getPublishedAt());
        assertNull(event.getPayload());
        verify(outboxEventRepository).save(event);
    }

    @Test
    void retainsFailedOutboxEventForRetry() {
        OutboxEvent event = createEvent();
        CompletableFuture<SendResult<String, Object>> failedSend = new CompletableFuture<>();
        failedSend.completeExceptionally(new IllegalStateException("Kafka unavailable"));
        when(outboxEventRepository.findTop100ByPublishedAtIsNullAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(any()))
                .thenReturn(List.of(event));
        when(kafkaTemplate.send(eq("payment.result"), eq("100"), any())).thenReturn(failedSend);

        publisher.publishPendingEvents();

        assertNull(event.getPublishedAt());
        assertNotNull(event.getPayload());
        assertEquals(1, event.getAttemptCount());
        assertNotNull(event.getLastError());
        verify(outboxEventRepository).save(event);
    }

    private OutboxEvent createEvent() {
        PaymentResultEvent payload = new PaymentResultEvent(
                "result-1", 1L, 100L, PaymentStatus.SUCCEEDED, null
        );
        try {
            return new OutboxEvent(
                    payload.eventId(), "payment.result", "100", payload.getClass().getName(),
                    new ObjectMapper().writeValueAsString(payload)
            );
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }
}