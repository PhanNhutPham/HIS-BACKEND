package com.booking.config.kafka;

import com.booking.event.MedicineOutOfStockEvent;
import com.booking.mapper.PrescriptionDetailJPA;
import com.booking.mapper.PrescriptionJPA;
import com.booking.models.entities.Prescription;
import com.booking.models.entities.PrescriptionDetail;
import com.booking.models.enums.PrescriptionStatus;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.*;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    private final PrescriptionDetailJPA prescriptionDetailRepository;
    private final PrescriptionJPA prescriptionRepository;

    public KafkaConsumerConfig(PrescriptionDetailJPA prescriptionDetailRepository, PrescriptionJPA prescriptionRepository) {
        this.prescriptionDetailRepository = prescriptionDetailRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @KafkaListener(topics = "medicine.out_of_stock", groupId = "prescription-service-group-v2")
    public void handleOutOfStock(MedicineOutOfStockEvent event) {
        String medicineId = event.getMedicineId();
        String medicineName = event.getMedicineName();
        int requiredQuantity = event.getRequiredQuantity();

        List<PrescriptionDetail> details = prescriptionDetailRepository.findByMedicineId(medicineId);
        Set<String> updatedPrescriptionIds = new HashSet<>();

        for (PrescriptionDetail detail : details) {
            Prescription prescription = detail.getPrescription();

            if (prescription != null && prescription.getStatus() == PrescriptionStatus.PENDING) {
                detail.setOutOfStock(true);

                if (!updatedPrescriptionIds.contains(prescription.getPrescriptionId())) {
                    prescription.setStatus(PrescriptionStatus.OUT_OF_STOCK);

                    String note = prescription.getPresDiagnosis() != null ? prescription.getPresDiagnosis() : "";
                    note += " | Thiếu thuốc: " + medicineName + " (" + requiredQuantity + ")";
                    prescription.setPresDiagnosis(note);

                    updatedPrescriptionIds.add(prescription.getPrescriptionId());

                    System.out.printf("Prescription ID %s được cập nhật trạng thái OUT_OF_STOCK do thiếu %s%n",
                            prescription.getPrescriptionId(), medicineName);
                }

                // Lưu prescription, cascade sẽ tự lưu detail nếu được cấu hình đúng
                prescriptionRepository.save(prescription);
            }
        }
    }

    @Bean
    public ConsumerFactory<String, MedicineOutOfStockEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9193");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "prescription-service-group-v2");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(MedicineOutOfStockEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MedicineOutOfStockEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MedicineOutOfStockEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
