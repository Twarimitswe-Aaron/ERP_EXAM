package com.rwanda.erp.payroll.repository;

import com.rwanda.erp.payroll.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface InstitutionRepository extends JpaRepository<Institution, UUID> {
}
