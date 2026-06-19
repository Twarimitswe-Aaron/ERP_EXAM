package com.rwanda.erp.payroll.service;

import com.rwanda.erp.payroll.entity.Deduction;
import com.rwanda.erp.payroll.entity.Employment;
import com.rwanda.erp.payroll.entity.Payslip;
import com.rwanda.erp.payroll.repository.PayslipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final EmployeeService employeeService;
    private final DeductionService deductionService;
    private final PayslipRepository payslipRepository;

    @Transactional
    public void generatePayroll(Integer month, Integer year) {
        List<Employment> activeEmployments = employeeService.getActiveEmployments();
        List<Deduction> deductions = deductionService.getAllDeductions();

        BigDecimal houseRate = getPercentage(deductionService.getDeductionByName("House", deductions));
        BigDecimal transportRate = getPercentage(deductionService.getDeductionByName("Transport", deductions));
        BigDecimal taxRate = getPercentage(deductionService.getDeductionByName("EmployeeTax", deductions));
        BigDecimal pensionRate = getPercentage(deductionService.getDeductionByName("Pension", deductions));
        BigDecimal medicalRate = getPercentage(deductionService.getDeductionByName("MedicalInsurance", deductions));
        BigDecimal othersRate = getPercentage(deductionService.getDeductionByName("Others", deductions));

        for (Employment employment : activeEmployments) {
            UUID empId = employment.getEmployee().getId();
            
            // Prevent duplicate payroll generation
            if (payslipRepository.existsByEmpIdAndMonthAndYear(empId, month, year)) {
                continue; // Skip already generated
            }

            BigDecimal baseSalary = employment.getBaseSalary();
            if (baseSalary == null) baseSalary = BigDecimal.ZERO;

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

    private BigDecimal getPercentage(Deduction deduction) {
        if (deduction == null || deduction.getPercentage() == null) return BigDecimal.ZERO;
        return deduction.getPercentage();
    }

    private BigDecimal calculateAmount(BigDecimal base, BigDecimal percentage) {
        return base.multiply(percentage).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}
