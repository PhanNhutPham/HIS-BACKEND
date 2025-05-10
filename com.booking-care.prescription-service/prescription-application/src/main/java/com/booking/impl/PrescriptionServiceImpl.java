package com.booking.impl;

import com.booking.event.PrescriptionDetailCreatedEvent;
import com.booking.mapper.PrescriptionDetailJPA;
import com.booking.mapper.PrescriptionJPA;
import com.booking.models.entities.Prescription;
import com.booking.models.entities.PrescriptionDetail;
import com.booking.models.enums.PrescriptionStatus;
import com.booking.request.PrescriptionDetailRequest;
import com.booking.request.PrescriptionRequest;
import com.booking.service.PrescriptionService;
import com.booking.service.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionJPA prescriptionRepository;
    private final PrescriptionDetailJPA prescriptionDetailRepository;
    private final KafkaProducerService
    kafkaProducerService;

    @Override
    @Transactional
    public Prescription createPrescription(Prescription prescription) {
        // Tạo danh sách PrescriptionDetail liên kết với Prescription
        List<PrescriptionDetail> detailList = new ArrayList<>();
        if (prescription.getPrescriptionDetails() != null) {
            for (PrescriptionDetail detail : prescription.getPrescriptionDetails()) {
                detail.setPrescription(prescription);
                detailList.add(detail);
            }
        }
        prescription.setPrescriptionDetails(detailList);
        return prescriptionRepository.save(prescription);
        // Lưu Prescription vào cơ sở dữ liệu
    }

    @Override
    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll(); // Trả về tất cả các Prescription từ cơ sở dữ liệu
    }

    @Override
    public Optional<Prescription> getPrescriptionById(String id) {
        return prescriptionRepository.findById(id); // Tìm Prescription theo ID
    }

    @Override
    @Transactional
    public Prescription updatePrescription(String id, Prescription prescription) {
        // Tìm Prescription theo ID
        return prescriptionRepository.findById(id).map(existingPrescription -> {
            existingPrescription.setDoctorId(prescription.getDoctorId());
            existingPrescription.setPatientId(prescription.getPatientId());
            existingPrescription.setPaymentStatus(prescription.getPaymentStatus());
            existingPrescription.setPresDiagnosis(prescription.getPresDiagnosis());
            existingPrescription.setPresDate(prescription.getPresDate());
            existingPrescription.setPresExpiryDate(prescription.getPresExpiryDate());

            // Xóa các PrescriptionDetail cũ và thay bằng các PrescriptionDetail mới
            // Xóa các PrescriptionDetail cũ và thay bằng các PrescriptionDetail mới
            existingPrescription.getPrescriptionDetails().clear();

            for (PrescriptionDetail detail : prescription.getPrescriptionDetails()) {
                detail.setPrescription(existingPrescription); // 👈 PHẢI set lại quan hệ cha
                existingPrescription.getPrescriptionDetails().add(detail);
            }

            return prescriptionRepository.save(existingPrescription); // Lưu bản cập nhật vào cơ sở dữ liệu
        }).orElseThrow(() -> new RuntimeException("Prescription not found with id: " + id)); // Nếu không tìm thấy Prescription theo ID
    }

    @Override
    public void deletePrescription(String id) {
        if (!prescriptionRepository.existsById(id)) {
            throw new RuntimeException("Prescription not found with id: " + id); // Nếu không tìm thấy Prescription theo ID
        }
        prescriptionRepository.deleteById(id); // Xóa Prescription theo ID
    }

    @Override
    @Transactional
    public Prescription createPrescriptionWithDetails(PrescriptionRequest request) {
        // 1. Tạo Prescription entity từ request
        Prescription prescription = Prescription.builder()
                .doctorId(request.getDoctorId())
                .patientId(request.getPatientId())
                .paymentStatus(request.getPaymentStatus())
                .presDiagnosis(request.getPresDiagnosis())
                .presDate(request.getPresDate())
                .presExpiryDate(request.getPresExpiryDate())
                .build();

        // 2. Lưu đơn thuốc để có prescriptionId
        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // 3. Danh sách chi tiết thuốc từ request
        List<PrescriptionDetailRequest> detailRequests = request.getPrescriptionDetails();
        List<PrescriptionDetail> savedDetails = new ArrayList<>();
        List<PrescriptionDetailCreatedEvent.MedicineUsage> medicineUsages = new ArrayList<>();

        for (PrescriptionDetailRequest detailRequest : detailRequests) {
            PrescriptionDetail detail = PrescriptionDetail.builder()
                    .medicineId(detailRequest.getMedicineId())
                    .medicineName(detailRequest.getMedicineName())
                    .dosage(detailRequest.getDosage())
                    .frequency(detailRequest.getPresDe_frequency())
                    .note(detailRequest.getPresDe_note())
                    .quanlity(Integer.valueOf(detailRequest.getQuantity())) // Đảm bảo quantity là Integer
                    .prescription(savedPrescription) // Gắn prescription cha
                    .build();

            PrescriptionDetail savedDetail = prescriptionDetailRepository.save(detail);
            savedDetails.add(savedDetail);
            if (savedPrescription.getPrescriptionDetails() == null) {
                savedPrescription.setPrescriptionDetails(new ArrayList<>());
            }

            // Thêm chi tiết vào prescriptionDetails của đơn thuốc
            savedPrescription.getPrescriptionDetails().add(savedDetail);  // <-- Quan trọng này

            // 4. Chuẩn bị data gửi event
            medicineUsages.add(new PrescriptionDetailCreatedEvent.MedicineUsage(
                    savedDetail.getMedicineId(),
                    savedDetail.getMedicineName(),
                    savedDetail.getQuanlity(),
                    savedDetail.getDosage(),
                    savedDetail.getFrequency(),
                    savedDetail.getNote()
            ));
        }

        // 5. Gửi sự kiện PrescriptionDetailCreatedEvent qua Kafka
        PrescriptionDetailCreatedEvent event = new PrescriptionDetailCreatedEvent(
                savedPrescription.getPrescriptionId(),
                PrescriptionStatus.PENDING,
                medicineUsages
        );
        kafkaProducerService.sendPrescriptionDetailCreatedEvent(event);

        // 6. Cập nhật lại đơn thuốc với các chi tiết đã lưu
        prescriptionRepository.save(savedPrescription);  // Đảm bảo prescription được lưu lại sau khi thêm details vào

        return savedPrescription;

    }




}
