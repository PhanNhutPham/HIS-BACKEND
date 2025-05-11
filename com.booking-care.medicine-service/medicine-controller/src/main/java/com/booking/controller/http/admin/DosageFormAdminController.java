package com.booking.controller.http.admin;

import com.booking.model.dto.request.DosageFormRequest;
import com.booking.model.dto.response.DosageFormResponse;
import com.booking.service.DosageFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/dosage-forms")
public class DosageFormAdminController {

    private final DosageFormService dosageFormService;

    @PostMapping("/add")
    public ResponseEntity<List<DosageFormResponse>> create(@RequestBody List<DosageFormRequest> requests) {
        return ResponseEntity.ok(dosageFormService.createDosageForms(requests));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<DosageFormResponse>> getAll() {
        return ResponseEntity.ok(dosageFormService.getAllDosageForms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DosageFormResponse> getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(dosageFormService.getDosageFormById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DosageFormResponse> update(@PathVariable("id") String id,
                                                     @RequestBody DosageFormRequest request) {
        return ResponseEntity.ok(dosageFormService.updateDosageForm(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable("id") String id) {
        dosageFormService.deleteDosageForm(id);
        return ResponseEntity.ok(Collections.singletonMap("message", "Xoá dạng bào chế thành công"));
    }
}