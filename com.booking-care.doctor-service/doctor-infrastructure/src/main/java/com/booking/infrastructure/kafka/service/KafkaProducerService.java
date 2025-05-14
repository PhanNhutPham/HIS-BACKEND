package com.booking.infrastructure.kafka.service;

import com.booking.infrastructure.kafka.event.CreateWorkScheduleEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, CreateWorkScheduleEvent> kafkaTemplate;

    private static final String TOPIC = "doctor.schedule.created";

    public String sendCreateWorkScheduleEvent(CreateWorkScheduleEvent event) {
        try {
            var result = kafkaTemplate.send(TOPIC, event).get();
            return "Event sent successfully. Topic: " + result.getRecordMetadata().topic() +
                    ", Partition: " + result.getRecordMetadata().partition() +
                    ", Offset: " + result.getRecordMetadata().offset();
        } catch (InterruptedException | ExecutionException e) {
            return "Failed to send CreateWorkScheduleEvent: " + e.getMessage();
        }
    }
}
