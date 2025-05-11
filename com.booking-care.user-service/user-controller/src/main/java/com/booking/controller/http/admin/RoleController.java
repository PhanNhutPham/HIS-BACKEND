package com.booking.controller.http.admin;

import com.booking.domain.models.entities.Role;
import com.booking.exceptions.DataNotFoundException;
import com.booking.model.dto.request.PermissionRequest;
import com.booking.model.dto.request.RoleRequest;
import com.booking.service.role.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.booking.model.dto.response.ResponseObject;
import com.booking.exceptions.ValidationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/roles")
public class RoleController {
    private final IRoleService roleService;

    @PostMapping("/add-role")
    public ResponseEntity<ResponseObject> createRole(@Valid @RequestBody RoleRequest roleRequest,
                                                     BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        if (roleRequest.getPermission_ids() == null) {
            roleRequest.setPermission_ids(new HashSet<>());
        }

        Role role = roleService.createRole(roleRequest);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(role)
                .message("Create Role successful")
                .build());
    }
    //    @PostMapping("/add-permission/{roleId}")
//    public ResponseEntity<ResponseObject> addPermissionToRole(@Valid
//                                                              @PathVariable("roleId") String roleId,
//                                                              @RequestBody Set<PermissionRequest> permissionRequests,
//                                                              BindingResult bindingResult) throws Exception {
//        if (bindingResult.hasErrors()) {
//            throw new ValidationException(bindingResult);
//        }
//
//        Role role = roleService.addPermissionToRole(roleId, permissionRequests);
//        return ResponseEntity.ok(ResponseObject.builder()
//                .status(HttpStatus.CREATED)
//                .data(role)
//                .message("Add permission to role successful")
//                .build());
//    }
    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllRoles() {
        List<Role> role = roleService.getAllRoles();
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Get list role successfully")
                        .data(role)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateRoleById(@PathVariable("id") String id,
                                                         @Valid @RequestBody RoleRequest roleRequest,
                                                         BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        Role updatedRole = roleService.updateRole(id, roleRequest);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Update role successfully")
                .data(updatedRole)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getRoleById(@PathVariable("id") String id) throws DataNotFoundException {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Get information role successfully")
                        .data(role)
                        .build());
    }

//    @DeleteMapping("/{roleId}/permission/{permissionId}")
//    public ResponseEntity<ResponseObject> deletePermissionFromRole(@PathVariable("roleId") String roleId, @PathVariable("permissionId") String permissionId) throws Exception {
//        roleService.deletePermissionInRole(roleId, permissionId);
//        return ResponseEntity.ok(
//                ResponseObject.builder()
//                        .status(HttpStatus.OK)
//                        .message("Delete permission from successfully")
//                        .build());
//    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<ResponseObject> deleteRole(@PathVariable("roleId") String roleId) throws DataNotFoundException {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Delete role successfully")
                        .build());
    }

}