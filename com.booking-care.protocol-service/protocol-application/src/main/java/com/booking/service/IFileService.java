package com.booking.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileService {
    void deleteFile(String fileName) throws IOException;

    boolean isImageFile(MultipartFile file) throws IOException;

    String storeFile(MultipartFile file) throws IOException;
}
