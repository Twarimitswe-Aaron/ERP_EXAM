package com.rwanda.erp.payroll.repository;

import com.rwanda.erp.payroll.entity.Department;
import com.rwanda.erp.payroll.entity.Position;
import com.rwanda.erp.payroll.entity.SalaryScale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SalaryScaleRepository extends JpaRepository<SalaryScale, UUID> {
    Optional<SalaryScale> findByInstitutionIdAndDepartmentAndPosition(UUID institutionId, Department department, Position position);
}
