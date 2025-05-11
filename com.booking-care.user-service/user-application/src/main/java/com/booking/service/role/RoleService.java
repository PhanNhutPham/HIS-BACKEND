package com.booking.service.role;

import com.booking.domain.models.entities.Permission;
import com.booking.domain.models.entities.Role;
import com.booking.domain.repositories.PermissionRepository;
import com.booking.domain.repositories.RoleRepository;
import com.booking.enums.ResultCode;
import com.booking.exceptions.DataNotFoundException;
import com.booking.exceptions.ExistsException;
import com.booking.model.dto.request.PermissionRequest;
import com.booking.model.dto.request.RoleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService{
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    @Override
    public Role createRole(RoleRequest roleRequest) throws Exception {
        Set<Permission> validPermissions = new HashSet<>();

        for (String permissionId : roleRequest.getPermission_ids()) {
            Optional<Permission> existingPermission = permissionRepository.findById(permissionId);
            if (existingPermission.isPresent()) {
                validPermissions.add(existingPermission.get());
            } else {
                throw new DataNotFoundException(ResultCode.PERMISSION_NOT_FOUND);

            }
        }

        Role role = Role.builder()
                .nameRole(roleRequest.getRole_name())
                .description(roleRequest.getRole_description())
                .permissions(validPermissions)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return roleRepository.save(role);
    }



    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleById(String id) throws DataNotFoundException {

        return roleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.ROLE_NOT_FOUND));
    }

    @Override
    public void deletePermissionInRole(String roleId, String permissionId) throws DataNotFoundException {
        int count = permissionRepository.countRolePermission(roleId, permissionId);

        if (count == 0) {
            throw new DataNotFoundException(ResultCode.PERMISSION_NOT_ASSIGNED_TO_ROLE);

        }

        permissionRepository.deleteFromRolePermission(roleId, permissionId);
    }

    @Transactional
    @Override
    public Role addPermissionToRole(String roleId,  Set<PermissionRequest> permissionsRequest) throws Exception {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.ROLE_NOT_FOUND));
        for (PermissionRequest permissionRequest : permissionsRequest) {
            Optional<Permission> permissionOptional = permissionRepository.findByNamePermissions(permissionRequest.getName_permission());
            if (permissionOptional.isEmpty()) {
                throw new DataNotFoundException(ResultCode.PERMISSION_NOT_FOUND);

            }

            Permission permission = permissionOptional.get();

            if (role.getPermissions().contains(permission)) {
                throw new ExistsException(ResultCode.PERMISSION_NOT_FOUND);

            }

            role.getPermissions().add(permission);
        }
        role.setUpdatedAt(LocalDateTime.now());

        return roleRepository.save(role);
    }

//    @Transactional
//    @Override
//    public Role updateRole(String roleId, RoleRequest roleRequest) throws DataNotFoundException {
//        Role role = roleRepository.findById(roleId)
//                .orElseThrow(() -> new DataNotFoundException(ResultCode.ROLE_NOT_FOUND));
//
//
//        Optional.ofNullable(roleRequest.getRole_name())
//                .filter(role_name -> !role_name.isEmpty())
//                .ifPresent(role::setNameRole);
//
//        Optional.ofNullable(roleRequest.getRole_description())
//                .filter(role_description -> !role_description.isEmpty())
//                .ifPresent(role::setDescription);
//
//        role.setUpdatedAt(LocalDateTime.now());
//
//        return roleRepository.save(role);
//    }

//    @Transactional
//    @Override
//    public void deleteRole(String roleId) throws DataNotFoundException {
//        Role role = roleRepository.findById(roleId)
//                .orElseThrow(() -> new DataNotFoundException(ResultCode.ROLE_NOT_FOUND));
//
//        List<Permission> permissions = permissionRepository.findByRole(role); // <- Sửa chỗ này
//
//        if (!permissions.isEmpty()) {
//            throw new IllegalStateException("Cannot delete role with associated permissions");
//        } else {
//            roleRepository.deleteById(roleId);
//        }
//    }

    @Override
    public Role updateRole(String id, RoleRequest roleRequest) throws DataNotFoundException {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.ROLE_NOT_FOUND));

        existingRole.setNameRole(roleRequest.getRole_name());
        existingRole.setDescription(roleRequest.getRole_description());
        existingRole.setUpdatedAt(LocalDateTime.now());

        // Cập nhật danh sách permission nếu có
        Set<Permission> updatedPermissions = new HashSet<>();
        if (roleRequest.getPermission_ids() != null) {
            for (String permissionId : roleRequest.getPermission_ids()) {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new DataNotFoundException(ResultCode.PERMISSION_NOT_FOUND));
                updatedPermissions.add(permission);
            }
            existingRole.setPermissions(updatedPermissions);
        }

        return roleRepository.save(existingRole);
    }

    @Override
    public void deleteRole(String roleId) throws DataNotFoundException {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.ROLE_NOT_FOUND));

        roleRepository.delete(role);
    }

}