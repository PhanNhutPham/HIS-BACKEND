package com.booking.service;

import com.booking.domain.models.entities.MedicalRecord;

import java.util.List;

public interface MedicalRecordService {

    /**
     * Tạo mới một MedicalRecord.
     *
     * @param medicalRecord Đối tượng MedicalRecord cần tạo mới
     * @return MedicalRecord sau khi được tạo và lưu vào database
     */
    MedicalRecord createMedicalRecord(MedicalRecord medicalRecord);

    /**
     * Cập nhật thông tin của một MedicalRecord.
     *
     * @param id ID của MedicalRecord cần cập nhật
     * @param medicalRecord Đối tượng MedicalRecord với các thay đổi cần cập nhật
     * @return MedicalRecord đã được cập nhật
     */
    MedicalRecord updateMedicalRecord(String id, MedicalRecord medicalRecord);

    /**
     * Lấy MedicalRecord theo ID.
     *
     * @param id ID của MedicalRecord
     * @return MedicalRecord nếu tìm thấy, null nếu không tìm thấy
     */
    MedicalRecord getMedicalRecordById(String id);

    /**
     * Lấy tất cả MedicalRecord.
     *
     * @return Danh sách tất cả MedicalRecord
     */
    List<MedicalRecord> getAllMedicalRecords();

    /**
     * Xóa MedicalRecord theo ID.
     *
     * @param id ID của MedicalRecord cần xóa
     */
    void deleteMedicalRecord(String id);
}
