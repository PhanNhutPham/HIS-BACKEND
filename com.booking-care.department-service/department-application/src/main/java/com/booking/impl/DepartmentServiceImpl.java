package com.booking.impl;

import com.booking.domain.models.entities.Department;
import com.booking.persistence.mapper.DepartmentJPA;
import com.booking.services.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentJPA departmentRepository;

    public DepartmentServiceImpl(DepartmentJPA departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Department getDepartment(String id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + id));
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department updateDepartment(String id, Department updatedDepartment) {
        Department department = getDepartment(id);
        department.setName(updatedDepartment.getName());
        department.setRooms(updatedDepartment.getRooms()); // Nếu cần cập nhật các phòng
        return departmentRepository.save(department);
    }

    @Override
    public void deleteDepartment(String id) {
        departmentRepository.deleteById(id);
    }
    @Override
    public Department getDepartmentWithRooms(String departmentId) {
        return departmentRepository.findByIdWithRooms(departmentId);
    }


}
