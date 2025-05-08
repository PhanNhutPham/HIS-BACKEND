package com.booking.persistence.mapper;

import com.booking.domain.models.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentJPA extends JpaRepository<Department, String> {

    // Thêm @Param cho tham số departmentId trong query
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.rooms WHERE d.department_id = :departmentId")
    Department findByIdWithRooms(@Param("departmentId") String departmentId);

}
