package com.booking.controller.http.file;


import com.booking.service.file.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/files")
public class FileController {
    private final IFileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filename = fileService.storeFile(file);
            return ResponseEntity.ok(filename);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Could not store file: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        try {
            fileService.deleteFile(filename);
            return ResponseEntity.ok("File deleted successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Could not delete file: " + e.getMessage());
        }
    }
}