package com.rwanda.erp.payroll.controller;

import com.rwanda.erp.payroll.entity.Payslip;
import com.rwanda.erp.payroll.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generatePayroll(
            @RequestParam Integer month, 
            @RequestParam Integer year) {
        payrollService.generatePayroll(month, year);
        return ResponseEntity.ok("Payroll generation triggered for " + month + "/" + year);
    }

    @GetMapping("/my-payslips")
    public ResponseEntity<List<Payslip>> getMyPayslips(Authentication authentication) {
        String email = authentication.getName(); // JWT subject is the email
        return ResponseEntity.ok(payrollService.getMyPayslips(email));
    }
}
