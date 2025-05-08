package com.booking.service.permission;

import com.booking.domain.models.entities.Permission;
import com.booking.domain.repositories.PermissionRepository;
import com.booking.enums.ResultCode;
import com.booking.exceptions.DataNotFoundException;
import com.booking.exceptions.ExistsException;
import com.booking.model.dto.request.PermissionRequest;
import com.booking.model.dto.response.PermissionPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionService implements IPermissionService{
    private final PermissionRepository permissionRepository;

    //PERMISSION_ALREADY_EXISTS
    @Transactional
    @Override
    public Permission createPermission(PermissionRequest permissionRequest) throws ExistsException {
        Optional<Permission> permissionOptional =
                permissionRepository.findByNamePermissions(permissionRequest.getName_permission());

        if (permissionOptional.isPresent()) {
            throw new ExistsException(ResultCode.PERMISSION_NOT_FOUND);
        }

        Permission permission = Permission.builder()
                .namePermissions(permissionRequest.getName_permission())
                .description(permissionRequest.getDescription_permission())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return permissionRepository.save(permission);
    }

    @Override
    public PermissionPageResponse getAllPermission(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        List<String> allowedFields = List.of(
                "permission_id",
                "namePermissions",
                "description",
                "createdAt",
                "updatedAt"
        );

        if (sortBy == null || !allowedFields.contains(sortBy)) {
            sortBy = "permissionId";
        }

        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Permission> permissionPage = permissionRepository.findAll(pageable);
        return getPermissionPageResponse(pageNumber, pageSize, permissionPage);
    }



    private PermissionPageResponse getPermissionPageResponse(Integer pageNumber, Integer pageSize, Page<Permission> permissionsPage) {
        List<Permission> permissions = permissionsPage.getContent();

        if(permissions.isEmpty()) {
            return new PermissionPageResponse(null, 0, 0, 0, 0, true);
        }

        List<Permission> productList = new ArrayList<>(permissions);

        int totalPages = permissionsPage.getTotalPages();
        int totalElements = (int) permissionsPage.getTotalElements();
        boolean isLast = permissionsPage.isLast();

        return new PermissionPageResponse(productList, pageNumber, pageSize, totalElements, totalPages, isLast);
    }

    @Override
    public Permission getPermissionById(String id) throws DataNotFoundException {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.PERMISSION_NOT_FOUND));
    }

    @Transactional
    @Override
    public Permission updatePermission(String permissionId, PermissionRequest permissionRequest) throws DataNotFoundException {
        Permission existingPermission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.PERMISSION_NOT_FOUND));

        Optional.ofNullable(permissionRequest.getName_permission())
                .filter(name_permission -> !name_permission.isEmpty())
                .ifPresent(existingPermission::setNamePermissions);

        Optional.ofNullable(permissionRequest.getDescription_permission())
                .filter(description_permission -> !description_permission.isEmpty())
                .ifPresent(existingPermission::setDescription);

        existingPermission.setUpdatedAt(LocalDateTime.now());
        return permissionRepository.save(existingPermission);
    }

    @Transactional
    @Override
    public void deletePermission(String permissionId) throws DataNotFoundException {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.PERMISSION_NOT_FOUND));

        int count = permissionRepository.countRolePermissionsByPermissionId(permissionId);

        if (count > 0) {
            throw new IllegalStateException("Cannot delete permission because it is assigned to a role");
        }

        permissionRepository.delete(permission);
    }
}
