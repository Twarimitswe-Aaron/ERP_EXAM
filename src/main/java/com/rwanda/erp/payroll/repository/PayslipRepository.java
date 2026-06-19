package com.rwanda.erp.payroll.repository;

import com.rwanda.erp.payroll.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PayslipRepository extends JpaRepository<Payslip, UUID> {
    boolean existsByEmployeeIdAndMonthAndYear(UUID employeeId, Integer month, Integer year);
    List<Payslip> findByEmployeeId(UUID employeeId);
}
