package com.booking.service.role;

import com.booking.domain.models.entities.Role;
import com.booking.exceptions.DataNotFoundException;
import com.booking.model.dto.request.PermissionRequest;
import com.booking.model.dto.request.RoleRequest;

import java.util.List;
import java.util.Set;

public interface IRoleService {
    Role createRole(RoleRequest roleRequest) throws Exception;
    List<Role> getAllRoles();
    Role getRoleById(String id) throws DataNotFoundException;
    void deletePermissionInRole(String roleId, String permissionId) throws DataNotFoundException;
    Role addPermissionToRole(String roleId, Set<PermissionRequest> permissionsRequest) throws Exception;
    Role updateRole(String roleId, RoleRequest roleRequest) throws DataNotFoundException;
    void deleteRole(String roleId) throws DataNotFoundException;
}
