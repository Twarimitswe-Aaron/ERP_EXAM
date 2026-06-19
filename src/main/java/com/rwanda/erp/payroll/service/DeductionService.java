package com.rwanda.erp.payroll.service;

import com.rwanda.erp.payroll.entity.Deduction;
import com.rwanda.erp.payroll.repository.DeductionRepository;
import lombok.RequiredArgsConstructor;
import com.rwanda.erp.payroll.entity.DeductionType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeductionService {

    private final DeductionRepository deductionRepository;

    @CacheEvict(value = "deductions", allEntries = true)
    public Deduction saveDeduction(Deduction deduction) {
        Deduction existing = deductionRepository.findByType(deduction.getType());
        if (existing != null) {
            existing.setPercentage(deduction.getPercentage());
            return deductionRepository.save(existing);
        }
        return deductionRepository.save(deduction);
    }

    @Cacheable(value = "deductions")
    public List<Deduction> getAllDeductions() {
        return deductionRepository.findAll();
    }

    public Deduction getDeductionByType(DeductionType type, List<Deduction> deductions) {
        return deductions.stream()
                .filter(d -> d.getType() == type)
                .findFirst()
                .orElse(null);
    }
}
