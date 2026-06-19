# Enterprise Payroll Management System

This is the backend system for the Government of Rwanda Enterprise Resource Planning (ERP) project. It provides robust Employee Management and Payroll Generation.

## Technology Stack
- Java 17, Spring Boot 3.2.5
- PostgreSQL (Primary Data Store)
- Redis (Caching for Employees and Deductions)
- Spring Security with JWT Authentication
- Spring Data JPA
- Docker & Docker Compose

## User Journey
```mermaid
journey
    title Admin & Employee User Journey
    section Authentication
      Admin/Employee logs in: 5: Admin, Employee
      System issues JWT token: 5: System
    section Admin: Employee Management
      Admin creates Employee profile: 4: Admin
      Admin assigns Employment details & Base Salary: 4: Admin
    section Admin: Payroll Generation
      Admin triggers Payroll (Month/Year): 5: Admin
      System calculates Gross/Net from Base Salary: 5: System
      System applies DB Trigger -> sends Message, marks Paid: 5: System
    section Employee: View Payslip
      Employee checks Payslip history: 4: Employee
      Employee views specific Payslip details & Deductions: 4: Employee
```

## Setup Instructions

1. **Start the Infrastructure**
   ```bash
   docker-compose up -d
   ```
   This spins up PostgreSQL on port `5432` and Redis on port `6379`.

2. **Run the Application**
   ```bash
   ./mvnw spring-boot:run
   ```
   The application will run on `http://localhost:8080`.

3. **Database Initialization**
   Upon first start, Hibernate will automatically create the tables, and `schema.sql` will execute to create the **Database Trigger** (`payslip_insert_trigger`) required to send the automated Message.

## API Documentation

### Authentication (`/api/auth`)
- `POST /api/auth/register` - Create a new user (Role: ADMIN or EMPLOYEE).
- `POST /api/auth/login` - Authenticate and receive a JWT Bearer token.

### Employee Management (`/api/employees`) - Admin Only
- `POST /api/employees` - Register a new employee.
- `GET /api/employees` - List all employees.
- `PUT /api/employees/{id}/employment` - Set employment info (Department, Position, Base Salary).

### Deductions (`/api/deductions`) - Admin Only
- `POST /api/deductions` - Configure deduction percentages (EmployeeTax: 30%, Pension: 6%, MedicalInsurance: 5%, Others: 5%, House: 14%, Transport: 14%).
- `GET /api/deductions` - List current deduction configurations.

### Payroll Management (`/api/payroll`)
- `POST /api/payroll/generate?month=X&year=YYYY` - Trigger payroll generation for all active employees. Calculates Gross and Net salaries and saves the payslips. (Admin Only).
- `GET /api/payroll/employee/{empId}` - View payslips for a given employee.

## Database Trigger (Task 5)
When the Admin triggers payroll generation, Payslips are saved to the PostgreSQL database with the status `Generated`. 

A `BEFORE INSERT` trigger in PostgreSQL intercepts this and automatically:
1. Formats a message: `"Dear <FIRSTNAME> Your salary of <MONTH/YEAR> from <institution> <amount> has been credited to your <employee ID> account successfully"`
2. Inserts it into the `message` table with a `UUID`.
3. Updates the Payslip status to `Paid`.
