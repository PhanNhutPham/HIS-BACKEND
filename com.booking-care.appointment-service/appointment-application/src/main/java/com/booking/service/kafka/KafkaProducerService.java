package com.booking.service.kafka;

import com.booking.event.AppointmentConfirmed;
import com.booking.event.AppointmentRequestInitiated;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String APPOINTMENT_REQUEST_INITIATED = "appointment.request.initiated";
    private static final String APPOINTMENT_CONFIRM="appointment.confirm";// Topic name

    public String sendUserCreatedEvent(AppointmentRequestInitiated event) {
        try {
            // Gửi sự kiện và đợi kết quả (chờ cho đến khi Kafka trả lời)
            var result = kafkaTemplate.send(APPOINTMENT_REQUEST_INITIATED, event).get();  // Đây sẽ chờ đến khi có phản hồi từ Kafka

            // Nếu gửi thành công, trả về thông tin thành công
            return "Event sent successfully. Topic: " + result.getRecordMetadata().topic() +
                    ", Partition: " + result.getRecordMetadata().partition() +
                    ", Offset: " + result.getRecordMetadata().offset();

        } catch (InterruptedException | ExecutionException e) {
            // Nếu gặp lỗi khi gửi, thông báo lỗi
            return "Failed to send event: " + e.getMessage();
        }

    }
    public String sendAppointmentConfirmEvent(AppointmentConfirmed event) {
        try {
            var result = kafkaTemplate.send(APPOINTMENT_CONFIRM, event).get();

            return "Event sent successfully. Topic: " + result.getRecordMetadata().topic() +
                    ", Partition: " + result.getRecordMetadata().partition() +
                    ", Offset: " + result.getRecordMetadata().offset();

        } catch (InterruptedException | ExecutionException e) {
            return "Failed to send event: " + e.getMessage();
        }
    }

}
