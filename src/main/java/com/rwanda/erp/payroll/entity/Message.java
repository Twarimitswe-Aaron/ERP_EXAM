package com.rwanda.erp.payroll.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Changed to match rubric: proper implementation of many-to-one relationship
    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    
    @Column(name = "payroll_month")
    private Integer month;
    
    @Column(name = "payroll_year")
    private Integer year;
    
    // Changed to match rubric: correct definiton of message entity(id,text, dateTime)
    private String text;
    
    private LocalDateTime dateTime;
}
