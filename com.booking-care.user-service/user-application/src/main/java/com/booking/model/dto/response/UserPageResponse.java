package com.booking.model.dto.response;

import com.booking.domain.models.entities.User;

import java.util.List;

public class UserPageResponse {
    private List<User> users;
    private Integer pageNumber;
    private Integer pageSize;
    private int totalElements;
    private int totalPages;
    private boolean isLast;

    public UserPageResponse() {}

    public UserPageResponse(List<User> users, Integer pageNumber, Integer pageSize,
                            int totalElements, int totalPages, boolean isLast) {
        this.users = users;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.isLast = isLast;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
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
