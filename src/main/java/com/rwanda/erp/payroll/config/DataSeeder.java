package com.rwanda.erp.payroll.config;

import com.rwanda.erp.payroll.entity.Institution;
import com.rwanda.erp.payroll.entity.User;
import com.rwanda.erp.payroll.repository.InstitutionRepository;
import com.rwanda.erp.payroll.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.UUID;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        String triggerSql = """
            DROP TRIGGER IF EXISTS trg_after_payslip_insert ON payslip;
            DROP TRIGGER IF EXISTS trg_before_payslip_insert ON payslip;
            DROP FUNCTION IF EXISTS process_payslip_trigger();

            CREATE OR REPLACE FUNCTION process_payslip_trigger()
            RETURNS TRIGGER AS $$
            DECLARE
                emp_first_name VARCHAR;
                inst_name VARCHAR;
                msg_body TEXT;
            BEGIN
                SELECT e.first_name, i.name
                INTO emp_first_name, inst_name
                FROM employee e
                JOIN employment emp ON emp.employee_id = e.id
                JOIN institution i ON emp.institution_id = i.id
                WHERE e.id = NEW.employee_id;

                NEW.status := 'Paid';

                msg_body := 'Dear ' || COALESCE(emp_first_name, 'Employee') || 
                            ' Your salary of ' || NEW.payroll_month || '/' || NEW.payroll_year || 
                            ' from ' || COALESCE(inst_name, 'Institution') || ' ' || NEW.net_salary || 
                            ' has been credited to your ' || NEW.employee_id || ' account Successfully.';

                INSERT INTO message (id, employee_id, text, date_time, payroll_month, payroll_year)
                VALUES (gen_random_uuid(), NEW.employee_id, msg_body, CURRENT_TIMESTAMP, NEW.payroll_month, NEW.payroll_year);

                RETURN NEW;
            END;
            $$ LANGUAGE plpgsql;

            CREATE TRIGGER trg_before_payslip_insert
            BEFORE INSERT ON payslip
            FOR EACH ROW
            EXECUTE FUNCTION process_payslip_trigger();
        """;
        jdbcTemplate.execute(triggerSql);

        if (!userRepository.existsByEmail(adminEmail)) {
            Institution govt = institutionRepository.findByName("Government of Rwanda")
                    .orElseGet(() -> {
                        Institution newGovt = new Institution();
                        newGovt.setName("Government of Rwanda");
                        return institutionRepository.save(newGovt);
                    });

            User superAdmin = User.builder()
                    .email(adminEmail)
                    // Password satisfies: Min 5 chars, Upper, Lower, Number, Special
                    .password(passwordEncoder.encode(adminPassword))
                    .role("SUPER_ADMIN")
                    .institution(govt)
                    .build();

            userRepository.save(superAdmin);
            System.out.println("Super Admin created: " + adminEmail + " / " + adminPassword);
        }
    }
}
