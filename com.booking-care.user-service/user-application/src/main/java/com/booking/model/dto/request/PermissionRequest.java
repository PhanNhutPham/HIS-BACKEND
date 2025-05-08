package com.booking.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRequest {
    @NotBlank
    @Size(min = 3, max = 35, message = "Permission name must be between 3 and 35 characters")
    private String name_permission;

    @Size(max = 150, message = "Description permission must be max 150 characters")
    private String description_permission;
}
