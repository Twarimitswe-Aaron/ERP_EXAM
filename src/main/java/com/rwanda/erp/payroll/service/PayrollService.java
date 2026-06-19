package com.rwanda.erp.payroll.service;

import com.rwanda.erp.payroll.entity.Deduction;
import com.rwanda.erp.payroll.entity.DeductionType;
import com.rwanda.erp.payroll.entity.Employment;
import com.rwanda.erp.payroll.entity.*;
import com.rwanda.erp.payroll.repository.PayslipRepository;
import com.rwanda.erp.payroll.repository.MessageRepository;
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
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional
    public void generatePayroll(Integer month, Integer year, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail).orElseThrow();
        Institution institution = admin.getInstitution();
        if (institution == null) throw new IllegalArgumentException("Admin has no assigned institution");

        List<Employment> activeEmployments = employeeService.getActiveEmploymentsByInstitution(institution).stream()
                .filter(emp -> emp.getEmployee().getUser() == null || !"ADMIN".equals(emp.getEmployee().getUser().getRole()))
                .toList();
        
        processPayrollList(activeEmployments, month, year);
    }

    @Transactional
    public void generateAdminPayrolls(Integer month, Integer year) {
        // Super admin processes payroll for ALL Admins across ALL institutions
        List<Employment> adminEmployments = employeeRepository.findAll().stream()
                .filter(emp -> "Active".equals(emp.getStatus()) && emp.getEmployment() != null)
                .filter(emp -> emp.getUser() != null && "ADMIN".equals(emp.getUser().getRole()))
                .map(Employee::getEmployment)
                .toList();
                
        processPayrollList(adminEmployments, month, year);
    }

    private void processPayrollList(List<Employment> employments, Integer month, Integer year) {
        List<Deduction> deductions = deductionService.getAllDeductions();

        BigDecimal houseRate = getPercentageWithFallback(deductionService.getDeductionByType(DeductionType.HOUSE, deductions), "14");
        BigDecimal transportRate = getPercentageWithFallback(deductionService.getDeductionByType(DeductionType.TRANSPORT, deductions), "14");
        BigDecimal taxRate = getPercentageWithFallback(deductionService.getDeductionByType(DeductionType.EMPLOYEE_TAX, deductions), "30");
        BigDecimal pensionRate = getPercentageWithFallback(deductionService.getDeductionByType(DeductionType.PENSION, deductions), "6");
        BigDecimal medicalRate = getPercentageWithFallback(deductionService.getDeductionByType(DeductionType.MEDICAL_INSURANCE, deductions), "5");
        BigDecimal othersRate = getPercentageWithFallback(deductionService.getDeductionByType(DeductionType.OTHERS, deductions), "4");

        for (Employment employment : employments) {
            UUID empId = employment.getEmployee().getId();
            
            // Prevent duplicate payroll generation
            if (payslipRepository.existsByEmployeeIdAndMonthAndYear(empId, month, year)) {
                continue; // Skip already generated
            }

            BigDecimal baseSalary = employment.getEmployee().getBaseSalary();
            if (baseSalary == null) baseSalary = BigDecimal.ZERO;

            BigDecimal house = calculateAmount(baseSalary, houseRate);
            BigDecimal transport = calculateAmount(baseSalary, transportRate);
            BigDecimal gross = baseSalary.add(house).add(transport);

            BigDecimal tax = calculateAmount(baseSalary, taxRate);
            BigDecimal pension = calculateAmount(baseSalary, pensionRate);
            BigDecimal medical = calculateAmount(baseSalary, medicalRate);
            
            // Exam Rule: 45% total deductions. Since Tax(30) + Pension(6) + Medical(5) = 41%, Others must be exactly 4%.
            BigDecimal other = calculateAmount(baseSalary, othersRate);
            
            BigDecimal totalDeductions = tax.add(pension).add(medical).add(other);
            
            // Exam Rule: Make sure that all deductions don't exceed the Gross salary
            if (totalDeductions.compareTo(gross) > 0) {
                totalDeductions = gross;
            }

            // NetSalary = Gross Salary - Total Deductions (as proven by the 57,800 example in the prompt)
            BigDecimal netSalary = gross.subtract(totalDeductions);

            Payslip payslip = new Payslip();
            payslip.setEmployee(employment.getEmployee());
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
        return payslipRepository.findByEmployeeId(empId);
    }

    public List<Payslip> getMyPayslips(String email) {
        Employee employee = employeeService.findByUserEmail(email);
        if (employee == null) {
            throw new EntityNotFoundException("No employee profile found for user: " + email);
        }
        return payslipRepository.findByEmployeeId(employee.getId());
    }

    public List<Message> getMyMessages(String email) {
        Employee employee = employeeService.findByUserEmail(email);
        if (employee == null) {
            throw new EntityNotFoundException("No employee profile found for user: " + email);
        }
        return messageRepository.findByEmployeeId(employee.getId());
    }

    private BigDecimal getPercentageWithFallback(Deduction deduction, String fallbackStr) {
        if (deduction == null || deduction.getPercentage() == null) return new BigDecimal(fallbackStr);
        return deduction.getPercentage();
    }

    private BigDecimal calculateAmount(BigDecimal base, BigDecimal percentage) {
        return base.multiply(percentage).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}
