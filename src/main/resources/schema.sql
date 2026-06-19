-- Drop existing trigger and function if they exist to allow clean recreation
DROP TRIGGER IF EXISTS trg_after_payslip_insert ON payslip;
DROP TRIGGER IF EXISTS trg_before_payslip_insert ON payslip;
DROP FUNCTION IF EXISTS process_payslip_trigger();

-- Create the trigger function
CREATE OR REPLACE FUNCTION process_payslip_trigger()
RETURNS TRIGGER AS $$
DECLARE
    emp_first_name VARCHAR;
    inst_name VARCHAR;
    msg_body TEXT;
BEGIN
    -- Fetch the employee's first name and institution name
    SELECT e.first_name, i.name
    INTO emp_first_name, inst_name
    FROM employee e
    JOIN employment emp ON emp.employee_id = e.id
    JOIN institution i ON emp.institution_id = i.id
    WHERE e.id = NEW.employee_id;

    -- Update status to Paid
    NEW.status := 'Paid';

    -- Construct the exact format message
    -- Dear <FIRSTNAME> Your <salary of MONTH/YEAR> from <INSTITUTION> <AMOUNT> has been credited to your <EMPLOYEE ID> account Successfully.
    msg_body := 'Dear ' || COALESCE(emp_first_name, 'Employee') || 
                ' Your salary of ' || NEW.payroll_month || '/' || NEW.payroll_year || 
                ' from ' || COALESCE(inst_name, 'Institution') || ' ' || NEW.net_salary || 
                ' has been credited to your ' || NEW.employee_id || ' account Successfully.';

    -- Insert into message table
    INSERT INTO message (id, employee_id, text, date_time, payroll_month, payroll_year)
    VALUES (gen_random_uuid(), NEW.employee_id, msg_body, CURRENT_TIMESTAMP, NEW.payroll_month, NEW.payroll_year);

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger that fires BEFORE INSERT
CREATE TRIGGER trg_before_payslip_insert
BEFORE INSERT ON payslip
FOR EACH ROW
EXECUTE FUNCTION process_payslip_trigger();
