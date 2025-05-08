package com.booking.model.dto.response;

import com.booking.domain.models.entities.Permission;

import java.util.List;

public class PermissionPageResponse  {
    private List<Permission> permissions;
    private Integer pageNumber;
    private Integer pageSize;
    private int totalElements;
    private int totalPages;
    private boolean isLast;

    public PermissionPageResponse() {}

    public PermissionPageResponse(List<Permission> permissions, Integer pageNumber, Integer pageSize,
                                  int totalElements, int totalPages, boolean isLast) {
        this.permissions = permissions;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.isLast = isLast;
    }

    // Getters and setters

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }
}
