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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : "EMPLOYEE")
                .build();
        userRepository.save(user);
        var jwtToken = jwtUtils.generateToken(user);
        return ResponseEntity.ok(AuthResponse.builder().token(jwtToken).build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtUtils.generateToken(user);
        return ResponseEntity.ok(AuthResponse.builder().token(jwtToken).build());
    }
}
