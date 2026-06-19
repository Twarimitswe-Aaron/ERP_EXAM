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

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private DeductionType type; // e.g., EMPLOYEE_TAX, PENSION

    @Column(nullable = false)
    private BigDecimal percentage;
}
