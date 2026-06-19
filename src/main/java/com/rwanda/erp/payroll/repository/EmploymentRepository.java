package com.rwanda.erp.payroll.repository;

import com.rwanda.erp.payroll.entity.Employment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EmploymentRepository extends JpaRepository<Employment, UUID> {
    List<Employment> findByStatus(String status);
}
