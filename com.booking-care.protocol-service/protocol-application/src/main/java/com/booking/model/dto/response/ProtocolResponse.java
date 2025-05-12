package com.booking.model.dto.response;


import com.booking.domain.models.entities.Protocol;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProtocolResponse {
    private String protocolId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String gender;
    private String avatarProtocol;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String userId;

    public ProtocolResponse(Protocol protocol) {
        this.protocolId = protocol.getProtocolId();
        this.fullName = protocol.getFullName();
        this.email = protocol.getEmail();
        this.phoneNumber = protocol.getPhoneNumber();
        this.gender = protocol.getGender();
        this.avatarProtocol = protocol.getAvatarProtocol();
        this.createTime = protocol.getCreateTime();
        this.updateTime = protocol.getUpdateTime();
        this.userId = protocol.getUserId();
    }
}

