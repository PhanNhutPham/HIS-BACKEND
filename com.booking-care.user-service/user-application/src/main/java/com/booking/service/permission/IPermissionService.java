package com.booking.service.permission;

import com.booking.domain.models.entities.Permission;
import com.booking.exceptions.DataNotFoundException;
import com.booking.model.dto.request.PermissionRequest;
import com.booking.model.dto.response.PermissionPageResponse;

public interface IPermissionService {
    Permission createPermission(PermissionRequest permissionRequest) throws Exception;
    PermissionPageResponse getAllPermission(Integer pageNumber, Integer pageSize, String sortBy, String dir);
    Permission getPermissionById(String id) throws DataNotFoundException;
    Permission updatePermission(String permissionId, PermissionRequest permissionRequest) throws DataNotFoundException;
    void deletePermission(String permissionId) throws DataNotFoundException;
}
