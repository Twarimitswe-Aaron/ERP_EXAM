package com.rwanda.erp.payroll.controller;

import com.rwanda.erp.payroll.entity.Deduction;
import com.rwanda.erp.payroll.service.DeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deductions")
@RequiredArgsConstructor
public class DeductionController {

    private final DeductionService deductionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Deduction> saveDeduction(@RequestBody Deduction deduction) {
        return ResponseEntity.ok(deductionService.saveDeduction(deduction));
    }

    @GetMapping
    public ResponseEntity<List<Deduction>> getAllDeductions() {
        return ResponseEntity.ok(deductionService.getAllDeductions());
    }
}
