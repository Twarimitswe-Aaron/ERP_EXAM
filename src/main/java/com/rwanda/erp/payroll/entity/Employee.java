package com.rwanda.erp.payroll.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

import java.util.UUID;

@Entity
@Table(name = "employee")
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;
    private String lastName;
    private String email;
    private String district;
    private String mobile;
    private LocalDate dateOfBirth;
    
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Employment employment;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
