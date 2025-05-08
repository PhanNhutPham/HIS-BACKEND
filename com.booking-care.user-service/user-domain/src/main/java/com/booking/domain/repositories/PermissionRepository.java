package com.booking.domain.repositories;

import com.booking.domain.models.entities.Permission;
import com.booking.domain.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository  extends JpaRepository<Permission, String> {
    Optional<Permission> findByNamePermissions(String name);

    // Sử dụng tên method chuẩn của Spring Data JPA
    List<Permission> findByRole(Role role);

    // Đếm số lượng role-permission theo permissionId
    @Query(value = "SELECT COUNT(1) FROM role_permission WHERE permission_id = :permissionId", nativeQuery = true)
    int countRolePermissionsByPermissionId(@Param("permissionId") String permissionId);

    // Đếm role-permission cụ thể (role_id & permission_id đều là String)
    @Query(value = "SELECT COUNT(*) FROM role_permission WHERE role_id = :roleId AND permission_id = :permissionId", nativeQuery = true)
    int countRolePermission(@Param("roleId") String roleId, @Param("permissionId") String permissionId);

    // Xóa role-permission cụ thể
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM role_permission WHERE role_id = :roleId AND permission_id = :permissionId", nativeQuery = true)
    void deleteFromRolePermission(@Param("roleId") String roleId, @Param("permissionId") String permissionId);
}
