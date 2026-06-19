package com.rwanda.erp.payroll.controller;

import com.rwanda.erp.payroll.dto.AuthRequest;
import com.rwanda.erp.payroll.dto.AuthResponse;
import com.rwanda.erp.payroll.entity.User;
import com.rwanda.erp.payroll.repository.UserRepository;
import com.rwanda.erp.payroll.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller responsible for user authentication and registration.
 * Handles the generation of JWT tokens.
 */
import com.rwanda.erp.payroll.dto.RegisterRequest;
import com.rwanda.erp.payroll.entity.Employee;
import com.rwanda.erp.payroll.repository.EmployeeRepository;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Operation(summary = "Register new employee", description = "Registers a new user and returns a JWT token. Automatically assigns EMPLOYEE role.")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already registered!");
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("EMPLOYEE") // Force all public registrations to be EMPLOYEE
                .build();
        userRepository.save(user);
        
        var employee = new Employee();
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setUser(user);
        employee.setStatus("Active");
        employeeRepository.save(employee);
        
        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        extraClaims.put("role", user.getRole());
        
        var jwtToken = jwtUtils.generateToken(extraClaims, user);
        return ResponseEntity.ok(AuthResponse.builder().token(jwtToken).build());
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user via email/password and returns a JWT token.")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        
        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        extraClaims.put("role", user.getRole());
        
        var jwtToken = jwtUtils.generateToken(extraClaims, user);
        return ResponseEntity.ok(AuthResponse.builder().token(jwtToken).build());
    }
}
