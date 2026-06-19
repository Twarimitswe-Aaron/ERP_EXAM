package com.rwanda.erp.payroll.repository;

import com.rwanda.erp.payroll.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByUserEmail(String email);
    java.util.List<Employee> findByEmploymentInstitutionAndStatus(com.rwanda.erp.payroll.entity.Institution institution, String status);
}
