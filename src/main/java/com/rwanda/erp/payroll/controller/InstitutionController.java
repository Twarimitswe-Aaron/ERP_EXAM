package com.rwanda.erp.payroll.controller;

import com.rwanda.erp.payroll.entity.Institution;
import com.rwanda.erp.payroll.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequestMapping("/api/institutions")
@RequiredArgsConstructor
@Tag(name = "Institutions", description = "Endpoints for managing institutions")
public class InstitutionController {

    private final InstitutionRepository institutionRepository;

    @GetMapping
    @Operation(summary = "List all institutions")
    public ResponseEntity<List<Institution>> getAllInstitutions() {
        return ResponseEntity.ok(institutionRepository.findAll());
    }

    @PostMapping
    @Operation(summary = "Create a new institution")
    public ResponseEntity<?> createInstitution(@RequestBody Institution institution) {
        if (institution.getName() == null || institution.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Institution name is required.");
        }
        
        if (institutionRepository.findByName(institution.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Institution with this name already exists.");
        }
        
        var saved = institutionRepository.save(institution);
        return ResponseEntity.ok(saved);
    }
}
