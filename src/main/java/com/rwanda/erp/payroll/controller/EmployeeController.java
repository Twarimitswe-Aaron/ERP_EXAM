package com.rwanda.erp.payroll.controller;

import com.rwanda.erp.payroll.entity.Employee;
import com.rwanda.erp.payroll.entity.Employment;
import com.rwanda.erp.payroll.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PutMapping("/{id}/employment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Employee> updateEmployment(
            @PathVariable UUID id, 
            @RequestBody Employment employment) {
        return ResponseEntity.ok(employeeService.updateEmployment(id, employment));
    }
}
