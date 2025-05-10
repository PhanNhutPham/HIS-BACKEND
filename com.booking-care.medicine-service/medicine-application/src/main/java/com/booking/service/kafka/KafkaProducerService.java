package com.booking.service.kafka;



import com.booking.event.MedicineOutOfStockEvent;
import com.booking.event.PrescriptionApprovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String OUT_OF_STOCK = "medicine.out_of_stock"; // Topic name
    private static final String PRESCRIPTION_APPROVED = "prescription.approved";
    public void sendPrescriptionApprovedEvent(PrescriptionApprovedEvent event) throws ExecutionException, InterruptedException {
        kafkaTemplate.send(PRESCRIPTION_APPROVED, event).get(); // Đảm bảo gửi sự kiện thành công
    }

    // Gửi sự kiện MedicineOutOfStock
    public void sendMedicineOutOfStockEvent(MedicineOutOfStockEvent event) throws ExecutionException, InterruptedException {
        kafkaTemplate.send(OUT_OF_STOCK, event).get(); // Đảm bảo gửi sự kiện thành công
    }

}
