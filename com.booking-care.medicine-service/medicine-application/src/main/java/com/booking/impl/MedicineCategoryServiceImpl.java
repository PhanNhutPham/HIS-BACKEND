package com.booking.impl;

import com.booking.domain.models.entities.MedicineCategory;

import com.booking.infrastructure.persistence.mapper.MedicineCategoryJPA;
import com.booking.model.dto.request.MedicineCategoryRequest;
import com.booking.model.dto.response.MedicineCategoryResponse;
import com.booking.service.MedicineCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineCategoryServiceImpl implements MedicineCategoryService{

    private final MedicineCategoryJPA medicineCategoryJPA;

    @Override
    public List<MedicineCategoryResponse> createCategories(List<MedicineCategoryRequest> requests) {
        List<MedicineCategoryResponse> responses = new ArrayList<>();

        for (MedicineCategoryRequest request : requests) {
            MedicineCategory category = MedicineCategory.builder()
                    .medicineCategoryName(request.getMedicineCategoryName())
                    .createTime(LocalDateTime.now())  // ✅ dùng LocalDateTime
                    .updateTime(LocalDateTime.now())
                    .build();

            MedicineCategory savedCategory = medicineCategoryJPA.save(category);

            MedicineCategoryResponse response = new MedicineCategoryResponse();
            response.setMedicineCategoryId(savedCategory.getMedicineCategoryId());
            response.setMedicineCategoryName(savedCategory.getMedicineCategoryName());
            response.setCreateTime(savedCategory.getCreateTime());
            response.setUpdateTime(savedCategory.getUpdateTime());

            responses.add(response);
        }

        return responses;
    }


    @Override
    public List<MedicineCategoryResponse> getAllCategories() {
        List<MedicineCategory> categories = medicineCategoryJPA.findAll();
        return categories.stream().map(category -> {
            MedicineCategoryResponse response = new MedicineCategoryResponse();
            response.setMedicineCategoryId(category.getMedicineCategoryId());
            response.setMedicineCategoryName(category.getMedicineCategoryName());
            response.setCreateTime(category.getCreateTime());
            response.setUpdateTime(category.getUpdateTime());
            return response;
        }).toList();
    }

    @Override
    public MedicineCategoryResponse getCategoryById(String id) {
        MedicineCategory category = medicineCategoryJPA.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));

        MedicineCategoryResponse response = new MedicineCategoryResponse();
        response.setMedicineCategoryId(category.getMedicineCategoryId());
        response.setMedicineCategoryName(category.getMedicineCategoryName());
        response.setCreateTime(category.getCreateTime());
        response.setUpdateTime(category.getUpdateTime());

        return response;
    }

    @Override
    public MedicineCategoryResponse updateCategory(String id, MedicineCategoryRequest request) {
        MedicineCategory category = medicineCategoryJPA.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));

        category.setMedicineCategoryName(request.getMedicineCategoryName());
        category.setUpdateTime(LocalDateTime.now());

        MedicineCategory saved = medicineCategoryJPA.save(category);

        MedicineCategoryResponse response = new MedicineCategoryResponse();
        response.setMedicineCategoryId(saved.getMedicineCategoryId());
        response.setMedicineCategoryName(saved.getMedicineCategoryName());
        response.setCreateTime(saved.getCreateTime());
        response.setUpdateTime(saved.getUpdateTime());

        return response;
    }

    @Override
    public void deleteCategory(String id) {
        MedicineCategory category = medicineCategoryJPA.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
        medicineCategoryJPA.delete(category);
    }
}
