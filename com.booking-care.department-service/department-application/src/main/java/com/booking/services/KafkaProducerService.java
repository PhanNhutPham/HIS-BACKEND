package com.booking.services;

import com.booking.request.AssignDoctorEvent;
import com.booking.request.DepartmentEventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String DEPARTMENT_CREATE = "department.create"; // Topic name
    private static final String ASSIGN_DOCTOR_TOPIC = "assign-doctor-department-topic";
    // Phương thức gửi sự kiện bất đồng bộ mà không sử dụng callback
    public void sendDepartmentEventCreateAsync(DepartmentEventRequest event) {
        try {
            // Gửi sự kiện bất đồng bộ mà không chờ kết quả và không sử dụng callback
            kafkaTemplate.send(DEPARTMENT_CREATE, event);

            // Log thông tin gửi thành công
            logger.info("Event sent successfully to topic: {}", DEPARTMENT_CREATE);
        } catch (Exception e) {
            // Log bất kỳ lỗi nào xảy ra khi gửi sự kiện
            logger.error("Exception occurred while sending event: {}", e.getMessage());
        }
    }
    public void sendDoctorAssignmentEvent(AssignDoctorEvent event) {
        try {
            kafkaTemplate.send(ASSIGN_DOCTOR_TOPIC, event);
            logger.info("Sent doctor assignment event to topic: {}", ASSIGN_DOCTOR_TOPIC);
        } catch (Exception e) {
            logger.error("Failed to send doctor assignment event: {}", e.getMessage());
        }
    }

    // Phương thức gửi sự kiện đồng bộ nếu cần phải chờ kết quả
    public String sendDepartmentEventCreateSync(DepartmentEventRequest event) {
        try {
            // Gửi sự kiện và chờ kết quả đồng bộ
            var result = kafkaTemplate.send(DEPARTMENT_CREATE, event).get();

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
