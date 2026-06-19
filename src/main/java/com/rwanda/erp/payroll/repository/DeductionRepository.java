package com.rwanda.erp.payroll.repository;

import com.rwanda.erp.payroll.entity.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DeductionRepository extends JpaRepository<Deduction, UUID> {
    Deduction findByName(String name);
}
