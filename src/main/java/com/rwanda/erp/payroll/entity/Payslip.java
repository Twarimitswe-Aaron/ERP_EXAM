package com.rwanda.erp.payroll.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

import java.util.UUID;

@Entity
@Table(name = "payslip")
@Data
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID empId;
    private String name;
    
    private BigDecimal base;
    private BigDecimal house;
    private BigDecimal transport;
    private BigDecimal gross;
    
    private BigDecimal tax;
    private BigDecimal pension;
    private BigDecimal medical;
    private BigDecimal other;
    
    private BigDecimal netSalary;
    
    private String status; // "Generated" or "Paid"
    
    @Column(name = "payroll_month")
    private Integer month;
    
    @Column(name = "payroll_year")
    private Integer year;
}
