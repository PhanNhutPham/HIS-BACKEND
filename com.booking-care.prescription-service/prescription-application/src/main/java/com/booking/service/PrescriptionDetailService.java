package com.booking.service;

import com.booking.models.entities.PrescriptionDetail;
import com.booking.request.PrescriptionDetailRequest;

import java.util.List;
import java.util.Optional;

public interface PrescriptionDetailService {

    // Phương thức để tạo chi tiết đơn thuốc từ PrescriptionDetailRequest
    PrescriptionDetail createPrescriptionDetail(PrescriptionDetail request);

    // Lấy tất cả chi tiết đơn thuốc
    List<PrescriptionDetail> getAllPrescriptionDetails();

    // Tìm chi tiết đơn thuốc theo ID
    Optional<PrescriptionDetail> getPrescriptionDetailById(String id);

    // Cập nhật chi tiết đơn thuốc
    PrescriptionDetail updatePrescriptionDetail(String id, PrescriptionDetail request);

    // Xóa chi tiết đơn thuốc theo ID
    void deletePrescriptionDetail(String id);
}
