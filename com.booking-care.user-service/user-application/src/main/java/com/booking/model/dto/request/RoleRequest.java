package com.booking.model.dto.request;

import com.booking.domain.models.entities.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    @NotBlank
    @Size(min = 2, max = 35, message = "Role name must be between 4 and 35 characters")
    private String role_name;

    @Size(max = 150, message = "Description role must be max 150 characters")
    private String role_description;
    private Set<String> permission_ids = new HashSet<>();
}
