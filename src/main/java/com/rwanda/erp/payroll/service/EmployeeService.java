package com.rwanda.erp.payroll.service;

import com.rwanda.erp.payroll.entity.Employee;
import com.rwanda.erp.payroll.entity.Employment;
import com.rwanda.erp.payroll.repository.EmployeeRepository;
import com.rwanda.erp.payroll.repository.EmploymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmploymentRepository employmentRepository;

    @CacheEvict(value = "activeEmployees", allEntries = true)
    public Employee createEmployee(Employee employee) {
        if (employee.getEmployment() != null) {
            employee.getEmployment().setEmployee(employee);
        }
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Cacheable(value = "activeEmployees")
    public List<Employment> getActiveEmployments() {
        return employmentRepository.findByStatus("Active");
    }

    @CacheEvict(value = "activeEmployees", allEntries = true)
    public Employee updateEmployment(UUID employeeId, Employment employmentDetails) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        Employment employment = employee.getEmployment();
        if (employment == null) {
            employment = new Employment();
            employment.setEmployee(employee);
        }
        employment.setDepartment(employmentDetails.getDepartment());
        employment.setPosition(employmentDetails.getPosition());
        employment.setBaseSalary(employmentDetails.getBaseSalary());
        employment.setStatus(employmentDetails.getStatus());
        employment.setJoiningDate(employmentDetails.getJoiningDate());
        
        employee.setEmployment(employment);
        return employeeRepository.save(employee);
    }
}
