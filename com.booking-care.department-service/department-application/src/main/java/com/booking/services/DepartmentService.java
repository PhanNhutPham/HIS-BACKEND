package com.booking.services;

import com.booking.domain.models.entities.Department;

import java.util.List;

public interface DepartmentService {
    Department createDepartment(Department department);

    Department getDepartment(String id);
    Department getDepartmentWithRooms(String departmentId);
    List<Department> getAllDepartments();

    Department updateDepartment(String id, Department department);

    void deleteDepartment(String id);
}
