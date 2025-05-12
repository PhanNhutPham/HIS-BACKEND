package com.booking.impl;

import com.booking.domain.models.entities.Protocol;
import com.booking.enums.ResultCode;
import com.booking.exceptions.DataNotFoundException;
import com.booking.infrastructure.persistence.mapper.ProtocolJPA;
import com.booking.model.dto.response.ProtocolResponse;
import com.booking.service.IFileService;
import com.booking.service.ProtocolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProtocolServiceImpl implements ProtocolService {
    private final ProtocolJPA protocolRepository;
    private final IFileService fileService;
    @Override
    public Protocol createProtocol(Protocol protocol) {
        if (protocol == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }
        return protocolRepository.save(protocol);
    }

    @Override
    public List<ProtocolResponse> getAllProtocols() {
        List<Protocol> protocols = protocolRepository.findAll();

        return protocols.stream().map(protocol -> {
            ProtocolResponse response = new ProtocolResponse();
            response.setProtocolId(protocol.getProtocolId());
            response.setFullName(protocol.getFullName());
            response.setEmail(protocol.getEmail());
            response.setPhoneNumber(protocol.getPhoneNumber());
            response.setGender(protocol.getGender());
            response.setAvatarProtocol(protocol.getAvatarProtocol() != null ? protocol.getAvatarProtocol() : null);
            response.setCreateTime(protocol.getCreateTime());
            response.setUpdateTime(protocol.getUpdateTime());
            response.setUserId(protocol.getUserId() != null ? protocol.getUserId() : null);
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public ProtocolResponse updateProtocol(String id, ProtocolResponse request) {
        Protocol protocol = protocolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocol not found with ID: " + id));

        protocol.setFullName(request.getFullName());
        protocol.setEmail(request.getEmail());
        protocol.setPhoneNumber(request.getPhoneNumber());
        protocol.setGender(request.getGender());
        protocol.setUpdateTime(LocalDateTime.now());

        Protocol saved = protocolRepository.save(protocol);

        ProtocolResponse response = new ProtocolResponse();
        response.setProtocolId(saved.getProtocolId());
        response.setFullName(saved.getFullName());
        response.setEmail(saved.getEmail());
        response.setPhoneNumber(saved.getPhoneNumber());
        response.setGender(saved.getGender());
        response.setAvatarProtocol(saved.getAvatarProtocol());
        response.setCreateTime(saved.getCreateTime());
        response.setUpdateTime(saved.getUpdateTime());
        response.setUserId(saved.getUserId());

        return response;
    }

    @Override
    public void deleteProtocol(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Protocol ID cannot be null or empty");
        }

        if (!protocolRepository.existsById(id)) {
            throw new RuntimeException("Protocol not found with ID: " + id);
        }

        protocolRepository.deleteById(id);
    }

    @Override
    public void uploadAvatar(String protocolId, MultipartFile file) throws Exception {
        Protocol existingProtocol = protocolRepository.findById(protocolId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));

        validateFileUpload(file);

        try {
            String imageAvatar = fileService.storeFile(file);
            changeProtocolProfileImage(existingProtocol.getProtocolId(), imageAvatar);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload protocol avatar image: " + e.getMessage(), e);
        }
    }

    public void changeProtocolProfileImage(String protocolId, String avatarFileName) throws DataNotFoundException {
        Protocol protocol = protocolRepository.findById(protocolId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));

        protocol.setAvatarProtocol(avatarFileName);
        protocol.setUpdateTime(LocalDateTime.now());
        protocolRepository.save(protocol);
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
    public Protocol getProtocolByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        return protocolRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Protocol not found with user ID: " + userId));
    }

    @Override
    public ProtocolResponse updateProtocolByUserId(String userId, ProtocolResponse updatedProtocol) {
        Protocol protocol = protocolRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Protocol not found with userId: " + userId));

        if (updatedProtocol.getFullName() != null)
            protocol.setFullName(updatedProtocol.getFullName());

        if (updatedProtocol.getPhoneNumber() != null)
            protocol.setPhoneNumber(updatedProtocol.getPhoneNumber());

        if (updatedProtocol.getGender() != null)
            protocol.setGender(updatedProtocol.getGender());

        if (updatedProtocol.getEmail() != null)
            protocol.setEmail(updatedProtocol.getEmail());

        protocol.setUpdateTime(LocalDateTime.now());

        Protocol saved = protocolRepository.save(protocol);

        return new ProtocolResponse(saved);
    }

    @Override
    public void uploadAvatarByUserId(String userId, MultipartFile file) throws Exception {
        Protocol existingProtocol = protocolRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));

        validateFileUpload(file);

        try {
            String imageAvatar = fileService.storeFile(file);
            changeProtocolProfileImage(existingProtocol.getProtocolId(), imageAvatar);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload protocol avatar image: " + e.getMessage(), e);
        }
    }


}
