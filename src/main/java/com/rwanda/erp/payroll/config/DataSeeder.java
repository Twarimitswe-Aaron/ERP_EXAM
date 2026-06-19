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
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail(adminEmail)) {
            Institution govt = new Institution();
            govt.setName("Government of Rwanda");
            govt = institutionRepository.save(govt);

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
