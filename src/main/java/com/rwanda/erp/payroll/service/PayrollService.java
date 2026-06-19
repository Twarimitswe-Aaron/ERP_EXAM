package com.rwanda.erp.payroll.service;

import com.rwanda.erp.payroll.entity.Deduction;
import com.rwanda.erp.payroll.entity.DeductionType;
import com.rwanda.erp.payroll.entity.Employment;
import com.rwanda.erp.payroll.entity.*;
import com.rwanda.erp.payroll.repository.PayslipRepository;
import com.rwanda.erp.payroll.repository.SalaryScaleRepository;
import com.rwanda.erp.payroll.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final EmployeeService employeeService;
    private final DeductionService deductionService;
    private final PayslipRepository payslipRepository;
    private final SalaryScaleRepository salaryScaleRepository;
    private final UserRepository userRepository;

    @Transactional
    public void generatePayroll(Integer month, Integer year, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail).orElseThrow();
        Institution institution = admin.getInstitution();
        if (institution == null) throw new IllegalArgumentException("Admin has no assigned institution");

        List<Employment> activeEmployments = employeeService.getActiveEmploymentsByInstitution(institution);
        List<Deduction> deductions = deductionService.getAllDeductions();

        BigDecimal houseRate = getPercentage(deductionService.getDeductionByType(DeductionType.HOUSE, deductions));
        BigDecimal transportRate = getPercentage(deductionService.getDeductionByType(DeductionType.TRANSPORT, deductions));
        BigDecimal taxRate = getPercentage(deductionService.getDeductionByType(DeductionType.EMPLOYEE_TAX, deductions));
        BigDecimal pensionRate = getPercentage(deductionService.getDeductionByType(DeductionType.PENSION, deductions));
        BigDecimal medicalRate = getPercentage(deductionService.getDeductionByType(DeductionType.MEDICAL_INSURANCE, deductions));
        BigDecimal othersRate = getPercentage(deductionService.getDeductionByType(DeductionType.OTHERS, deductions));

        for (Employment employment : activeEmployments) {
            UUID empId = employment.getEmployee().getId();
            
            // Prevent duplicate payroll generation
            if (payslipRepository.existsByEmpIdAndMonthAndYear(empId, month, year)) {
                continue; // Skip already generated
            }

            SalaryScale scale = salaryScaleRepository.findByInstitutionIdAndDepartmentAndPosition(
                institution.getId(), employment.getDepartment(), employment.getPosition()
            ).orElseThrow(() -> new EntityNotFoundException("No salary scale configured for " + employment.getDepartment() + " - " + employment.getPosition()));

            BigDecimal baseSalary = scale.getBaseSalary();

            BigDecimal house = calculateAmount(baseSalary, houseRate);
            BigDecimal transport = calculateAmount(baseSalary, transportRate);
            BigDecimal gross = baseSalary.add(house).add(transport);

            BigDecimal tax = calculateAmount(baseSalary, taxRate);
            BigDecimal pension = calculateAmount(baseSalary, pensionRate);
            BigDecimal medical = calculateAmount(baseSalary, medicalRate);
            BigDecimal other = calculateAmount(baseSalary, othersRate);
            
            BigDecimal totalDeductions = tax.add(pension).add(medical).add(other);
            BigDecimal netSalary = baseSalary.subtract(totalDeductions); // Formula from prompt: NetSalary = baseSalary - deductions

            Payslip payslip = new Payslip();
            payslip.setEmpId(empId);
            payslip.setName(employment.getEmployee().getFirstName() + " " + employment.getEmployee().getLastName());
            payslip.setBase(baseSalary);
            payslip.setHouse(house);
            payslip.setTransport(transport);
            payslip.setGross(gross);
            payslip.setTax(tax);
            payslip.setPension(pension);
            payslip.setMedical(medical);
            payslip.setOther(other);
            payslip.setNetSalary(netSalary);
            payslip.setStatus("Generated"); // Database trigger will update this to "Paid"
            payslip.setMonth(month);
            payslip.setYear(year);

            payslipRepository.save(payslip);
        }
    }

    public List<Payslip> getPayslips(UUID empId) {
        return payslipRepository.findByEmpId(empId);
    }

    public List<Payslip> getMyPayslips(String email) {
        Employee employee = employeeService.findByUserEmail(email);
        if (employee == null) {
            throw new EntityNotFoundException("No employee profile found for user: " + email);
        }
        return payslipRepository.findByEmpId(employee.getId());
    }

    private BigDecimal getPercentage(Deduction deduction) {
        if (deduction == null || deduction.getPercentage() == null) return BigDecimal.ZERO;
        return deduction.getPercentage();
    }

    private BigDecimal calculateAmount(BigDecimal base, BigDecimal percentage) {
        return base.multiply(percentage).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}
