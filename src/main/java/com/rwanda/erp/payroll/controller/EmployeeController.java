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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for managing Employee profiles and HR information.
 */

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employee HR Management", description = "Endpoints for managing employee data and configurations")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create an Employee profile", description = "Creates the HR profile for a newly registered user.")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }

    @GetMapping
    @Operation(summary = "Get all Employees", description = "Retrieves a company directory list of all employees.")
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
