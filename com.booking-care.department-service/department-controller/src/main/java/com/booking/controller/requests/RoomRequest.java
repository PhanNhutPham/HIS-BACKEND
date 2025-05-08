package com.booking.controller.requests;

import lombok.Data;

@Data
public class RoomRequest {
    private String roomId;
    private String roomNumber;
    private String departmentId;
    private String departmentName;// hoặc thêm thông tin khác nếu cần, như loại phòng, số giường, v.v.
}
