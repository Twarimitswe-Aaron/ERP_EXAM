package com.rwanda.erp.payroll.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

import java.util.UUID;

@Entity
@Table(name = "deduction")
@Data
public class Deduction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name; // e.g., EmployeeTax, Pension, MedicalInsurance, Others, House, Transport

    @Column(nullable = false)
    private BigDecimal percentage;
}
