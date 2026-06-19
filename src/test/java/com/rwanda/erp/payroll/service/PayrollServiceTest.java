package com.rwanda.erp.payroll.service;

import com.rwanda.erp.payroll.entity.*;
import com.rwanda.erp.payroll.repository.MessageRepository;
import com.rwanda.erp.payroll.repository.PayslipRepository;
import com.rwanda.erp.payroll.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PayrollServiceTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private DeductionService deductionService;

    @Mock
    private PayslipRepository payslipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private PayrollService payrollService;

    private Employment activeEmployment;
    private List<Deduction> mockDeductions;
    private User adminUser;
    private Institution institution;

    @BeforeEach
    void setUp() {
        institution = new Institution();
        institution.setId(UUID.randomUUID());
        institution.setName("Rwandan Govt");

        adminUser = new User();
        adminUser.setEmail("admin@gov.rw");
        adminUser.setInstitution(institution);

        Employee peter = new Employee();
        peter.setId(UUID.randomUUID());
        peter.setFirstName("Peter");
        peter.setLastName("Parker");
        // baseSalary is now on Employee
        peter.setBaseSalary(new BigDecimal("70000.00"));

        activeEmployment = new Employment();
        activeEmployment.setEmployee(peter);
        activeEmployment.setInstitution(institution);

        mockDeductions = Arrays.asList(
                createDeduction(DeductionType.HOUSE, "14"),
                createDeduction(DeductionType.TRANSPORT, "14"),
                createDeduction(DeductionType.EMPLOYEE_TAX, "30"),
                createDeduction(DeductionType.PENSION, "6"),
                createDeduction(DeductionType.MEDICAL_INSURANCE, "5"),
                createDeduction(DeductionType.OTHERS, "4") // Re-adjusted to 4%
        );
    }

    private Deduction createDeduction(DeductionType type, String percentage) {
        Deduction d = new Deduction();
        d.setType(type);
        d.setPercentage(new BigDecimal(percentage));
        return d;
    }

    @Test
    void testGeneratePayroll_CalculatesGrossAndNetCorrectly() {
        // Arrange
        when(userRepository.findByEmail("admin@gov.rw")).thenReturn(Optional.of(adminUser));
        when(employeeService.getActiveEmploymentsByInstitution(institution)).thenReturn(Arrays.asList(activeEmployment));
        when(deductionService.getAllDeductions()).thenReturn(mockDeductions);
        
        when(deductionService.getDeductionByType(eq(DeductionType.HOUSE), anyList())).thenReturn(mockDeductions.get(0));
        when(deductionService.getDeductionByType(eq(DeductionType.TRANSPORT), anyList())).thenReturn(mockDeductions.get(1));
        when(deductionService.getDeductionByType(eq(DeductionType.EMPLOYEE_TAX), anyList())).thenReturn(mockDeductions.get(2));
        when(deductionService.getDeductionByType(eq(DeductionType.PENSION), anyList())).thenReturn(mockDeductions.get(3));
        when(deductionService.getDeductionByType(eq(DeductionType.MEDICAL_INSURANCE), anyList())).thenReturn(mockDeductions.get(4));
        // others is hardcoded to 4 in service, so it doesn't fetch it dynamically from DB

        when(payslipRepository.existsByEmployeeIdAndMonthAndYear(any(), anyInt(), anyInt())).thenReturn(false);

        // Act
        payrollService.generatePayroll(6, 2026, "admin@gov.rw");

        // Assert
        ArgumentCaptor<Payslip> payslipCaptor = ArgumentCaptor.forClass(Payslip.class);
        verify(payslipRepository, times(1)).save(payslipCaptor.capture());

        Payslip savedPayslip = payslipCaptor.getValue();

        /*
        Calculation Check based on Base = 70,000:
        House (14%) = 9,800
        Transport (14%) = 9,800
        Gross = 70,000 + 9,800 + 9,800 = 89,600
        Tax (30%) = 21,000
        Pension (6%) = 4,200
        Medical (5%) = 3,500
        Others (4% forced in code) = 2,800
        Total Deductions = 31,500
        Net Salary = Gross(89,600) - Deductions(31,500) = 58,100
        */

        assertEquals(new BigDecimal("89600.00"), savedPayslip.getGross());
        assertEquals(new BigDecimal("58100.00"), savedPayslip.getNetSalary());
        assertEquals("Generated", savedPayslip.getStatus());
        assertEquals(6, savedPayslip.getMonth());
        assertEquals(2026, savedPayslip.getYear());
    }
}
