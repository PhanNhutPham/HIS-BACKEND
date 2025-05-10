package com.booking.service;


import com.booking.model.dto.request.MedicineCategoryRequest;
import com.booking.model.dto.response.MedicineCategoryResponse;

import java.util.List;


public interface MedicineCategoryService {
    List<MedicineCategoryResponse> createCategories(List<MedicineCategoryRequest> requests);
    List<MedicineCategoryResponse> getAllCategories();
    MedicineCategoryResponse getCategoryById(String id);
    MedicineCategoryResponse updateCategory(String id, MedicineCategoryRequest request);
    void deleteCategory(String id);
}
