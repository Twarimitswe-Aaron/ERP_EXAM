package com.rwanda.erp.payroll.controller;

import com.rwanda.erp.payroll.dto.CreateAdminRequest;
import com.rwanda.erp.payroll.entity.Employee;
import com.rwanda.erp.payroll.entity.User;
import com.rwanda.erp.payroll.repository.EmployeeRepository;
import com.rwanda.erp.payroll.repository.InstitutionRepository;
import com.rwanda.erp.payroll.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Endpoints for Super Admin to manage Institution Admins")
public class AdminManagementController {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final InstitutionRepository institutionRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @Operation(summary = "List all Institution Admins")
    public ResponseEntity<List<User>> getAllAdmins() {
        return ResponseEntity.ok(
            userRepository.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRole()))
                .collect(Collectors.toList())
        );
    }

    @PostMapping
    @Operation(summary = "Create a new Institution Admin")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already registered!");
        }

        var institution = institutionRepository.findById(request.getInstitutionId());
        if (institution.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Institution not found!");
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ADMIN")
                .institution(institution.get())
                .build();
        userRepository.save(user);

        var employee = new Employee();
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setUser(user);
        employee.setStatus("Active");
        employeeRepository.save(employee);

        return ResponseEntity.ok(user);
    }
}
