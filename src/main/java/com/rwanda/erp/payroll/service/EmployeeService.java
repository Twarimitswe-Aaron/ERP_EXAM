package com.rwanda.erp.payroll.service;

import com.rwanda.erp.payroll.entity.Employee;
import com.rwanda.erp.payroll.entity.Employment;
import com.rwanda.erp.payroll.repository.EmployeeRepository;
import com.rwanda.erp.payroll.repository.EmploymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmploymentRepository employmentRepository;

    public Employee createEmployee(Employee employee) {
        if (employee.getEmployment() != null) {
            employee.getEmployment().setEmployee(employee);
        }
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employment> getActiveEmploymentsByInstitution(com.rwanda.erp.payroll.entity.Institution institution) {
        return employeeRepository.findByEmploymentInstitutionAndStatus(institution, "Active")
                .stream()
                .map(Employee::getEmployment)
                .toList();
    }

    private final com.rwanda.erp.payroll.repository.UserRepository userRepository;

    public Employee updateEmployment(UUID employeeId, com.rwanda.erp.payroll.dto.UpdateEmploymentRequest request, String adminEmail) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        com.rwanda.erp.payroll.entity.User adminUser = userRepository.findByEmail(adminEmail).orElseThrow();
        
        if (employee.getUser() != null && "ADMIN".equals(employee.getUser().getRole())) {
            if (employee.getUser().getEmail().equals(adminEmail)) {
                throw new SecurityException("Admins cannot modify their own employment records. Please contact a Super Admin.");
            }
        }
        
        Employment employment = employee.getEmployment();
        if (employment == null) {
            employment = new Employment();
            employment.setEmployee(employee);
            // Assign the employee to the admin's institution
            employment.setInstitution(adminUser.getInstitution());
            
            // Also update the User entity to belong to this institution
            if (employee.getUser() != null) {
                employee.getUser().setInstitution(adminUser.getInstitution());
            }
        }
        
        employment.setDepartment(request.getDepartment());
        employment.setPosition(request.getPosition());
        employment.setJoiningDate(request.getJoiningDate());
        
        employee.setEmployment(employment);
        employee.setBaseSalary(request.getBaseSalary() != null ? request.getBaseSalary() : employee.getBaseSalary());
        employee.setStatus(request.getStatus() != null ? request.getStatus() : employee.getStatus());
        return employeeRepository.save(employee);
    }

    public Employee findByUserEmail(String email) {
        return employeeRepository.findByUserEmail(email).orElse(null);
    }
}
