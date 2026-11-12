package com.yashmerino.ecommerce.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "outbox_events")
@Table(name = "outbox_events", indexes = {
        @Index(name = "idx_outbox_pending", columnList = "published_at,next_attempt_at,created_at")
})
@Getter
@NoArgsConstructor
public class OutboxEvent {

    @Id
    @Column(name = "event_id", length = 36)
    private String eventId;

    @Column(nullable = false)
    private String topic;

    @Column(name = "message_key", nullable = false)
    private String messageKey;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "next_attempt_at", nullable = false)
    private LocalDateTime nextAttemptAt;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    public OutboxEvent(String eventId, String topic, String messageKey, String eventType, String payload) {
        this.eventId = eventId;
        this.topic = topic;
        this.messageKey = messageKey;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
        this.nextAttemptAt = this.createdAt;
    }

    public void markPublished() {
        this.publishedAt = LocalDateTime.now();
        this.payload = null;
        this.lastError = null;
    }

    public void markFailed(Exception exception) {
        this.attemptCount++;
        long delaySeconds = Math.min(300L, 1L << Math.min(this.attemptCount, 8));
        this.nextAttemptAt = LocalDateTime.now().plusSeconds(delaySeconds);
        String message = exception.getMessage();
        this.lastError = message == null ? exception.getClass().getName() : message.substring(0, Math.min(message.length(), 2000));
    }
}