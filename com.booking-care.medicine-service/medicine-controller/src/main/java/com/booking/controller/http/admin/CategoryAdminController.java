package com.booking.controller.http.admin;

import com.booking.model.dto.request.MedicineCategoryRequest;
import com.booking.model.dto.response.MedicineCategoryResponse;
import com.booking.service.MedicineCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private final MedicineCategoryService medicineCategoryService ;

    // Thêm danh mục thuốc mới
    @PostMapping("/add-categories")
    public ResponseEntity<List<MedicineCategoryResponse>> createCategories(@RequestBody List<MedicineCategoryRequest> requests) {
        List<MedicineCategoryResponse> responses = medicineCategoryService.createCategories(requests);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<MedicineCategoryResponse>> getAllCategories() {
        List<MedicineCategoryResponse> categories = medicineCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineCategoryResponse> getCategoryById(@PathVariable("id") String id) {
        return ResponseEntity.ok(medicineCategoryService.getCategoryById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MedicineCategoryResponse> updateCategory(@PathVariable("id") String id,
                                                                   @RequestBody MedicineCategoryRequest request) {
        return ResponseEntity.ok(medicineCategoryService.updateCategory(id, request));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable("id") String id) {
        medicineCategoryService.deleteCategory(id);
        // Trả về JSON: {"message": "Xoá danh mục thuốc thành công"}
        return ResponseEntity.ok(Collections.singletonMap(
                "message", "Xoá danh mục thuốc thành công"
        ));
    }
}
