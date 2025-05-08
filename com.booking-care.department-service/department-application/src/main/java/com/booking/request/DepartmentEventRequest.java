package com.booking.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentEventRequest {
    private String departmentId; // ID của khoa
    private String departmentName; // Tên của khoa
    private String eventType; // Loại sự kiện (ví dụ: CREATE, UPDATE, DELETE)
}
