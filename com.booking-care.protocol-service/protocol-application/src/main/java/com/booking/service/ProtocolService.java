package com.booking.service;

import com.booking.domain.models.entities.Protocol;
import com.booking.model.dto.response.ProtocolResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProtocolService {
    Protocol createProtocol(Protocol protocol);
    List<ProtocolResponse> getAllProtocols();

    ProtocolResponse updateProtocol(String id, ProtocolResponse updatedProtocol);

    void deleteProtocol(String id);

    void uploadAvatar(String protocolId, MultipartFile file) throws Exception;
    Protocol getProtocolByUserId(String userId);
    ProtocolResponse updateProtocolByUserId(String userId, ProtocolResponse updatedProtocol);

    void uploadAvatarByUserId(String userId, MultipartFile file) throws Exception;
}
