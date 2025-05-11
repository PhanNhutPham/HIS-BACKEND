package com.booking.service;

import com.booking.model.dto.request.DosageFormRequest;
import com.booking.model.dto.response.DosageFormResponse;

import java.util.List;

public interface DosageFormService {
    List<DosageFormResponse> createDosageForms(List<DosageFormRequest> requests);
    List<DosageFormResponse> getAllDosageForms();
    DosageFormResponse getDosageFormById(String id);
    DosageFormResponse updateDosageForm(String id, DosageFormRequest request);
    void deleteDosageForm(String id);
}