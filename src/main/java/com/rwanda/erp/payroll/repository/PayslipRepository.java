package com.rwanda.erp.payroll.repository;

import com.rwanda.erp.payroll.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PayslipRepository extends JpaRepository<Payslip, UUID> {
    boolean existsByEmpIdAndMonthAndYear(UUID empId, Integer month, Integer year);
    List<Payslip> findByEmpId(UUID empId);
}
