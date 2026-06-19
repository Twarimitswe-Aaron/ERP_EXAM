-- PostgreSQL BEFORE INSERT Trigger for Payslip to send Message and update Status

CREATE OR REPLACE FUNCTION process_payslip_insert()
RETURNS TRIGGER AS $$
DECLARE
    emp_first_name VARCHAR;
    emp_department VARCHAR;
    msg_content TEXT;
BEGIN
    -- Get Employee Details
    SELECT first_name INTO emp_first_name FROM employee WHERE id = NEW.emp_id;
    SELECT department INTO emp_department FROM employment WHERE employee_id = NEW.emp_id;
    
    IF emp_department IS NULL THEN
        emp_department := 'Government of Rwanda';
    END IF;

    -- Update Payslip Status
    NEW.status := 'Paid';

    -- Format the message
    -- "Dear <FIRSTNAME> Your salary of <MONTH/YEAR> from <institustion> <amount> has been credited to your <employee ID> account successfully"
    msg_content := 'Dear ' || COALESCE(emp_first_name, 'Employee') || 
                   ' Your salary of ' || NEW.payroll_month || '/' || NEW.payroll_year || 
                   ' from ' || emp_department || ' ' || NEW.net_salary || 
                   ' has been credited to your ' || NEW.emp_id || ' account successfully';

    -- Insert into Message table (UUID generated automatically if using uuid_generate_v4() or Spring Boot does it, 
    -- but here we rely on DB sequence or we can use gen_random_uuid() which is built-in for PG 13+)
    INSERT INTO message (id, employee_id, payroll_month, payroll_year, content)
    VALUES (gen_random_uuid(), NEW.emp_id, NEW.payroll_month, NEW.payroll_year, msg_content);

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS payslip_insert_trigger ON payslip;

CREATE TRIGGER payslip_insert_trigger
BEFORE INSERT ON payslip
FOR EACH ROW
EXECUTE FUNCTION process_payslip_insert();
