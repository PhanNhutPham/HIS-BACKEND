package com.booking.service.kafka;

import com.booking.impl.MedicineWarehouseServiceImpl;
import org.springframework.kafka.annotation.EnableKafka;

import org.springframework.stereotype.Service;

@Service
@EnableKafka
public class KafkaConsumerService {
    private final MedicineWarehouseServiceImpl medicineWarehouseService;

    public KafkaConsumerService(MedicineWarehouseServiceImpl medicineWarehouseService) {
        this.medicineWarehouseService = medicineWarehouseService;
    }

//    @KafkaListener(topics = "prescription-detail-created", groupId = "medicine-service-group-v2")
//    public void consumeprescripCrate(PrescriptionDetailCreatedEvent event) {
//        for (PrescriptionDetailCreatedEvent.MedicineUsage medicineUsage : event.getMedicines()) {
//            String medicineId = medicineUsage.getMedicineId();
//            int requiredQuantity = medicineUsage.getQuantity();
//
//            // Kiểm tra kho thuốc xem có đủ số lượng không
//            boolean isInStock = medicineWarehouseService.checkStock(medicineId, requiredQuantity);
//
//            if (!isInStock) {
//                // Nếu thuốc không đủ số lượng, gửi sự kiện MedicineOutOfStockEvent
//                MedicineOutOfStockEvent outOfStockEvent = new MedicineOutOfStockEvent(
//                        medicineId,
//                        medicineUsage.getMedicineName(),
//                        requiredQuantity
//                );
//                // Gửi sự kiện MedicineOutOfStockEvent qua Kafka
//                sendMedicineOutOfStockEvent(outOfStockEvent);
//            }
//        }
//    }
//
//    // Gửi sự kiện MedicineOutOfStock
//    private void sendMedicineOutOfStockEvent(MedicineOutOfStockEvent event) {
//        // Giả sử có một producer service để gửi sự kiện ra Kafka
//        // kafkaProducerService.sendMedicineOutOfStockEvent(event);
//        System.out.println("Medicine out of stock: " + event);
//    }
    }

