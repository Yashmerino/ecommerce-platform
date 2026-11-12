package com.yashmerino.ecommerce.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void enqueue(String topic, String messageKey, Object payload) {
        enqueue(UUID.randomUUID().toString(), topic, messageKey, payload);
    }

    public void enqueue(String eventId, String topic, String messageKey, Object payload) {
        try {
            OutboxEvent event = new OutboxEvent(
                    eventId, topic, messageKey, payload.getClass().getName(), objectMapper.writeValueAsString(payload)
            );
            outboxEventRepository.save(event);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize outbox event", exception);
        }
    }
}