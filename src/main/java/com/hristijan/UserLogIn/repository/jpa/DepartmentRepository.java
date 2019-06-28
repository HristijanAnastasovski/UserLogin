package com.hristijan.UserLogIn.repository.jpa;

import com.hristijan.UserLogIn.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department findByDepartmentName(String departmentName);
}
