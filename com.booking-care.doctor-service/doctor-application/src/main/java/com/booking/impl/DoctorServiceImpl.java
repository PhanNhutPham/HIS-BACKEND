package com.booking.impl;

import com.booking.domain.models.entities.Doctor;
import com.booking.exceptions.DataNotFoundException;
import com.booking.exceptions.InvalidPasswordException;
import com.booking.model.dto.request.ResetPasswordRequest;
import com.booking.model.dto.response.DoctorResponse;
import com.booking.service.DoctorService;
import com.booking.infrastructure.persistence.mapper.DoctorJPA;
import com.booking.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.booking.enums.ResultCode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorJPA doctorRepository;
    private final IFileService fileService;


    @Override
    public Doctor createDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }
        return doctorRepository.save(doctor);
    }

//    @Override
//    public DoctorResponse getDoctor(String id) {
//        Doctor doctor = doctorRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));
//
//        DoctorResponse response = new DoctorResponse();
//        response.setDoctorId(doctor.getDoctorId());
//        response.setFullName(doctor.getFullName());
//        response.setEmail(doctor.getEmail());
//        response.setPhoneNumber(doctor.getPhoneNumber());
//        response.setGender(doctor.getGender());
//        response.setDegree(doctor.getDegree());
//        response.setAvatarDoctor(doctor.getAvatarDoctor() != null ? doctor.getAvatarDoctor() : null);
//        response.setCreateTime(doctor.getCreateTime() != null ? doctor.getCreateTime() : null);
//        response.setUpdateTime(doctor.getUpdateTime() != null ? doctor.getUpdateTime() : null);
//        response.setDepartmentId(doctor.getDepartment_id() != null ? doctor.getDepartment_id() : null);
//
//        return response;
//    }

    @Override
    public List<DoctorResponse> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();

        return doctors.stream().map(doctor -> {
            DoctorResponse response = new DoctorResponse();
            response.setDoctorId(doctor.getDoctorId());
            response.setFullName(doctor.getFullName());
            response.setEmail(doctor.getEmail());
            response.setPhoneNumber(doctor.getPhoneNumber());
            response.setGender(doctor.getGender());
            response.setDegree(doctor.getDegree());
            response.setAvatarDoctor(doctor.getAvatarDoctor() != null ? doctor.getAvatarDoctor() : null);
            response.setCreateTime(doctor.getCreateTime());
            response.setUpdateTime(doctor.getUpdateTime());
            response.setDepartmentId(doctor.getDepartment_id() != null ? doctor.getDepartment_id() : null);
            return response;
        }).collect(Collectors.toList());
    }


    @Override
    public DoctorResponse updateDoctor(String id, DoctorResponse request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));

        doctor.setFullName(request.getFullName());
        doctor.setEmail(request.getEmail());
        doctor.setPhoneNumber(request.getPhoneNumber());
        doctor.setGender(request.getGender());
        doctor.setDegree(request.getDegree());
        doctor.setUpdateTime(LocalDateTime.now());
        Doctor saved = doctorRepository.save(doctor);

        DoctorResponse resp = new DoctorResponse();
        resp.setDoctorId(saved.getDoctorId());
        resp.setFullName(saved.getFullName());
        resp.setEmail(saved.getEmail());
        resp.setPhoneNumber(saved.getPhoneNumber());
        resp.setGender(saved.getGender());
        resp.setDegree(saved.getDegree());
        resp.setAvatarDoctor(saved.getAvatarDoctor());
        resp.setCreateTime(saved.getCreateTime());
        resp.setUpdateTime(saved.getUpdateTime());

        return resp;
    }


    @Override
    public void deleteDoctor(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Doctor ID cannot be null or empty");
        }

        doctorRepository.deleteById(id);
    }

    @Override
    public Doctor assignDoctorToDepartment(String doctorId, String departmentId, String action) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));

        switch (action.toUpperCase()) {
            case "ASSIGN":
                if (departmentId == null || departmentId.isEmpty()) {
                    throw new IllegalArgumentException("Department ID cannot be null or empty for ASSIGN action.");
                }
                // ✅ Kiểm tra nếu đã được gán rồi thì bỏ qua
                if (departmentId.equals(doctor.getDepartment_id())) {
                    System.out.println("Doctor already assigned to this department. Skipping update.");
                    return doctor;
                }
                doctor.setDepartment_id(departmentId);
                break;

            case "UNASSIGN":
                if (doctor.getDepartment_id() == null) {
                    System.out.println("Doctor is already unassigned. Skipping update.");
                    return doctor;
                }
                doctor.setDepartment_id(null);
                break;

            default:
                throw new IllegalArgumentException("Invalid action: " + action);
        }

        return doctorRepository.save(doctor);
    }

    @Override
    public void uploadAvatar(String doctorId, MultipartFile file) throws Exception {
        Doctor existingDoctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));

        validateFileUpload(file);

        try {
            String imageAvatar = fileService.storeFile(file);
            changeDoctorProfileImage(existingDoctor.getDoctorId(), imageAvatar);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload doctor avatar image: " + e.getMessage(), e);
        }
    }

    @Override
    public void uploadAvatarByUserId(String userId, MultipartFile file) throws Exception {
        Doctor existingDoctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));

        validateFileUpload(file);

        try {
            String imageAvatar = fileService.storeFile(file);
            changeDoctorProfileImage(existingDoctor.getDoctorId(), imageAvatar);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload doctor avatar image: " + e.getMessage(), e);
        }
    }

    public void changeDoctorProfileImage(String doctorId, String avatarFileName) throws DataNotFoundException {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND)); // dùng đúng mã lỗi

        doctor.setAvatarDoctor(avatarFileName);
        doctor.setUpdateTime(LocalDateTime.now());
        doctorRepository.save(doctor);
    }


    private void validateFileUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File is too large! Maximum size is 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
    }

    @Override
    public Doctor getDoctorByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with user ID: " + userId));
    }



    @Override
    public DoctorResponse updateDoctorByUserId(String userId, DoctorResponse updatedDoctor) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with userId: " + userId));

        if (updatedDoctor.getFullName() != null)
            doctor.setFullName(updatedDoctor.getFullName());


        if (updatedDoctor.getPhoneNumber() != null)
            doctor.setPhoneNumber(updatedDoctor.getPhoneNumber());

        if (updatedDoctor.getGender() != null)
            doctor.setGender(updatedDoctor.getGender());

        if (updatedDoctor.getDegree() != null)
            doctor.setDegree(updatedDoctor.getDegree());

        // Trường updateTime được cập nhật tự động nhờ @PreUpdate
        Doctor updated = doctorRepository.save(doctor);

        return new DoctorResponse(updated);
    }


}
