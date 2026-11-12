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

    public String enqueue(String eventId, String topic, String messageKey, Object payload) {
        try {
            outboxEventRepository.save(new OutboxEvent(
                    eventId == null ? UUID.randomUUID().toString() : eventId,
                    topic,
                    messageKey,
                    payload.getClass().getName(),
                    objectMapper.writeValueAsString(payload)
            ));
            return eventId;
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize outbox event", exception);
        }
    }
}