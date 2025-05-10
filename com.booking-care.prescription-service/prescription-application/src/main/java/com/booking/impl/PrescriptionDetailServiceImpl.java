package com.booking.impl;

import com.booking.mapper.PrescriptionDetailJPA;
import com.booking.mapper.PrescriptionJPA;
import com.booking.models.entities.Prescription;
import com.booking.models.entities.PrescriptionDetail;
import com.booking.service.PrescriptionDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrescriptionDetailServiceImpl implements PrescriptionDetailService {

    private final PrescriptionDetailJPA prescriptionDetailRepository;
    private final PrescriptionJPA prescriptionRepository; // 👈 thêm vào

    @Override
    @Transactional
    public PrescriptionDetail createPrescriptionDetail(PrescriptionDetail request) {
        // Giả sử bạn đã truyền prescriptionId từ phía client
        String prescriptionId = request.getPrescription().getPrescriptionId();
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        // Gán prescription cho detail
        request.setPrescription(prescription);

        return prescriptionDetailRepository.save(request);
    }

    @Override
    public List<PrescriptionDetail> getAllPrescriptionDetails() {
        // Lấy tất cả PrescriptionDetail từ cơ sở dữ liệu
        return prescriptionDetailRepository.findAll();
    }

    @Override
    public Optional<PrescriptionDetail> getPrescriptionDetailById(String id) {
        // Tìm PrescriptionDetail theo ID
        return prescriptionDetailRepository.findById(id);
    }

    @Override
    @Transactional
    public PrescriptionDetail updatePrescriptionDetail(String id, PrescriptionDetail request) {
        // Tìm PrescriptionDetail theo ID và cập nhật
        return prescriptionDetailRepository.findById(id).map(existingPrescriptionDetail -> {
            existingPrescriptionDetail.setMedicineName(request.getMedicineName());
            existingPrescriptionDetail.setDosage(request.getDosage());
            existingPrescriptionDetail.setQuanlity(request.getQuanlity());
            existingPrescriptionDetail.setFrequency(request.getFrequency());
            existingPrescriptionDetail.setNote(request.getNote());

            // Lưu PrescriptionDetail đã được cập nhật
            return prescriptionDetailRepository.save(existingPrescriptionDetail);
        }).orElseThrow(() -> new RuntimeException("PrescriptionDetail not found with id: " + id)); // Nếu không tìm thấy
    }

    @Override
    @Transactional
    public void deletePrescriptionDetail(String id) {
        // Kiểm tra xem PrescriptionDetail có tồn tại không
        if (!prescriptionDetailRepository.existsById(id)) {
            throw new RuntimeException("PrescriptionDetail not found with id: " + id);
        }
        // Xóa PrescriptionDetail theo ID
        prescriptionDetailRepository.deleteById(id);
    }
}
