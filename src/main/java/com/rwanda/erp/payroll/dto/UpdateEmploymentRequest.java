package com.rwanda.erp.payroll.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.rwanda.erp.payroll.entity.Department;
import com.rwanda.erp.payroll.entity.Position;

@Data
public class UpdateEmploymentRequest {
    private Department department;
    private Position position;
    private LocalDate joiningDate;
    private BigDecimal baseSalary;
    private String status;
}
