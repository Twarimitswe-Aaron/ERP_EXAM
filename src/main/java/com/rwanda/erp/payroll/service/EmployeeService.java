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

    public Employee updateEmployment(UUID employeeId, Employment employmentDetails) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        Employment employment = employee.getEmployment();
        if (employment == null) {
            employment = new Employment();
            employment.setEmployee(employee);
        }
        employment.setDepartment(employmentDetails.getDepartment());
        employment.setPosition(employmentDetails.getPosition());
        employment.setJoiningDate(employmentDetails.getJoiningDate());
        
        employee.setEmployment(employment);
        employee.setBaseSalary(employmentDetails.getEmployee() != null ? employmentDetails.getEmployee().getBaseSalary() : employee.getBaseSalary());
        employee.setStatus(employmentDetails.getEmployee() != null ? employmentDetails.getEmployee().getStatus() : employee.getStatus());
        return employeeRepository.save(employee);
    }

    public Employee findByUserEmail(String email) {
        return employeeRepository.findByUserEmail(email).orElse(null);
    }
}
