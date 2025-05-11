package com.booking.impl;

import com.booking.domain.models.entities.DosageForm;
import com.booking.infrastructure.persistence.mapper.DosageFormJPA;
import com.booking.model.dto.request.DosageFormRequest;
import com.booking.model.dto.response.DosageFormResponse;
import com.booking.service.DosageFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DosageFormServiceImpl implements DosageFormService {

    private final DosageFormJPA dosageFormJPA;

    @Override
    public List<DosageFormResponse> createDosageForms(List<DosageFormRequest> requests) {
        List<DosageFormResponse> responses = new ArrayList<>();
        for (DosageFormRequest request : requests) {
            DosageForm form = DosageForm.builder()
                    .dosageFormName(request.getDosageFormName())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            DosageForm saved = dosageFormJPA.save(form);

            DosageFormResponse response = new DosageFormResponse();
            response.setDosageFormId(saved.getDosageFormId());
            response.setDosageFormName(saved.getDosageFormName());
            response.setCreateTime(saved.getCreateTime());
            response.setUpdateTime(saved.getUpdateTime());

            responses.add(response);
        }
        return responses;
    }

    @Override
    public List<DosageFormResponse> getAllDosageForms() {
        List<DosageForm> forms = dosageFormJPA.findAll();
        return forms.stream().map(form -> {
            DosageFormResponse response = new DosageFormResponse();
            response.setDosageFormId(form.getDosageFormId());
            response.setDosageFormName(form.getDosageFormName());
            response.setCreateTime(form.getCreateTime());
            response.setUpdateTime(form.getUpdateTime());
            return response;
        }).toList();
    }

    @Override
    public DosageFormResponse getDosageFormById(String id) {
        DosageForm form = dosageFormJPA.findById(id)
                .orElseThrow(() -> new RuntimeException("Dosage form not found with ID: " + id));
        DosageFormResponse response = new DosageFormResponse();
        response.setDosageFormId(form.getDosageFormId());
        response.setDosageFormName(form.getDosageFormName());
        response.setCreateTime(form.getCreateTime());
        response.setUpdateTime(form.getUpdateTime());
        return response;
    }

    @Override
    public DosageFormResponse updateDosageForm(String id, DosageFormRequest request) {
        DosageForm form = dosageFormJPA.findById(id)
                .orElseThrow(() -> new RuntimeException("Dosage form not found with ID: " + id));

        form.setDosageFormName(request.getDosageFormName());
        form.setUpdateTime(LocalDateTime.now());
        DosageForm saved = dosageFormJPA.save(form);

        DosageFormResponse response = new DosageFormResponse();
        response.setDosageFormId(saved.getDosageFormId());
        response.setDosageFormName(saved.getDosageFormName());
        response.setCreateTime(saved.getCreateTime());
        response.setUpdateTime(saved.getUpdateTime());

        return response;
    }

    @Override
    public void deleteDosageForm(String id) {
        DosageForm form = dosageFormJPA.findById(id)
                .orElseThrow(() -> new RuntimeException("Dosage form not found with ID: " + id));
        dosageFormJPA.delete(form);
    }
}