package com.rwanda.erp.payroll.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "message")
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID employeeId;
    
    @Column(name = "payroll_month")
    private Integer month;
    
    @Column(name = "payroll_year")
    private Integer year;
    
    private String content;
}
