package com.rwanda.erp.payroll.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

@Entity
@Table(name = "employment")
@Data
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee employee;

    private String department;
    private String position;
    private BigDecimal baseSalary;
    private String status; // "Active" or "Inactive"
    private LocalDate joiningDate;
}
