package com.booking.controller.http.admin;


import com.booking.domain.models.entities.Permission;
import com.booking.exceptions.DataNotFoundException;
import com.booking.model.dto.request.PermissionRequest;
import com.booking.model.dto.response.PermissionPageResponse;
import com.booking.service.permission.IPermissionService;
import com.booking.utils.PageConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.booking.model.dto.response.ResponseObject;
import com.booking.exceptions.ValidationException;
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/permissions")
public class PermissionController {
    private final IPermissionService permissionService;

    @PostMapping("/add-permission")
    public ResponseEntity<ResponseObject> createPermission(@Valid @RequestBody PermissionRequest permissionRequest,
                                                           BindingResult bindingResult) throws Exception {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        Permission permission = permissionService.createPermission(permissionRequest);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(permission)
                .message("Create permission successful")
                .build());
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllPermissions(
            @RequestParam(name = "pageNumber", defaultValue = PageConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = PageConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "permissionSortBy", defaultValue = PageConstant.PERMISSION_SORT_BY) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = PageConstant.SORT_DIR) String sortDir
    ) {
        PermissionPageResponse permissionPageResponse = permissionService.getAllPermission(pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(permissionPageResponse)
                .message("Get list of permissions information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getPermissionById(@PathVariable("id") String id) throws DataNotFoundException {
        Permission permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(permission)
                .message("Get information permission successful")
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseObject> updatePermission(@Valid
                                                           @PathVariable("id") String id,
                                                           @RequestBody PermissionRequest permissionRequest) throws DataNotFoundException {
        Permission permissionUpdate = permissionService.updatePermission(id, permissionRequest);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(permissionUpdate)
                .message("Update permission successful")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable("id") String id) throws Exception {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(null)
                .message(String.format("Permission with id = %s deleted successfully", id))
                .status(HttpStatus.OK)
                .build());
    }


}
