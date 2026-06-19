package com.rwanda.erp.payroll.repository;

import com.rwanda.erp.payroll.entity.Deduction;
import com.rwanda.erp.payroll.entity.DeductionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DeductionRepository extends JpaRepository<Deduction, UUID> {
    Deduction findByType(DeductionType type);
}
