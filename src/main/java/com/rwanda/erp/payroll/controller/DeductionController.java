package com.rwanda.erp.payroll.controller;

import com.rwanda.erp.payroll.entity.Deduction;
import com.rwanda.erp.payroll.service.DeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for managing dynamic deduction percentages (e.g., Tax, Pension, Medical).
 * Only accessible by ADMIN users for configuration.
 */

@RestController
@RequestMapping("/api/deductions")
@RequiredArgsConstructor
@Tag(name = "Deductions Management", description = "Endpoints for configuring payroll tax and deduction percentages")
public class DeductionController {

    private final DeductionService deductionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Save or update a deduction", description = "Configure a deduction percentage. Requires ADMIN role.")
    public ResponseEntity<Deduction> saveDeduction(@RequestBody Deduction deduction) {
        return ResponseEntity.ok(deductionService.saveDeduction(deduction));
    }

    @GetMapping
    @Operation(summary = "Get all deductions", description = "Retrieves the list of active deduction percentages configured in the system.")
    public ResponseEntity<List<Deduction>> getAllDeductions() {
        return ResponseEntity.ok(deductionService.getAllDeductions());
    }
}
