package com.rwanda.erp.payroll.controller;

import com.rwanda.erp.payroll.entity.Payslip;
import com.rwanda.erp.payroll.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import com.rwanda.erp.payroll.dto.PayrollRequest;
import org.springframework.security.core.Authentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@Tag(name = "Payroll Management", description = "Endpoints for generating and viewing payslips")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generate monthly payroll", description = "Calculates deductions and generates payslips for all active employees. Only ADMIN can access.")
    public ResponseEntity<String> generatePayroll(
            @RequestBody PayrollRequest request, 
            Authentication authentication) {
        String email = authentication.getName();
        payrollService.generatePayroll(request.getMonth(), request.getYear(), email);
        return ResponseEntity.ok("Payroll generation triggered for " + request.getMonth() + "/" + request.getYear());
    }

    @GetMapping("/my-payslips")
    @Operation(summary = "Get my payslips", description = "Returns a detailed list of payslips for the authenticated employee.")
    public ResponseEntity<List<Payslip>> getMyPayslips(Authentication authentication) {
        String email = authentication.getName(); // JWT subject is the email
        return ResponseEntity.ok(payrollService.getMyPayslips(email));
    }
}
