package com.booking.service.kafka;


import com.booking.event.PrescriptionDetailCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String PRESCRIPTION_CREATED_TOPIC = "prescription-detail-created"; // Topic name

    public String sendPrescriptionDetailCreatedEvent(PrescriptionDetailCreatedEvent event) {
        try {
            // Gửi sự kiện và đợi kết quả (chờ cho đến khi Kafka trả lời)
            var result = kafkaTemplate.send(PRESCRIPTION_CREATED_TOPIC, event).get();  // Đây sẽ chờ đến khi có phản hồi từ Kafka

            // Nếu gửi thành công, trả về thông tin thành công
            return "Event sent successfully. Topic: " + result.getRecordMetadata().topic() +
                    ", Partition: " + result.getRecordMetadata().partition() +
                    ", Offset: " + result.getRecordMetadata().offset();

        } catch (InterruptedException | ExecutionException e) {
            // Nếu gặp lỗi khi gửi, thông báo lỗi
            return "Failed to send event: " + e.getMessage();
        }
    }
}
