package com.glowservices.glow_services_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.glowservices.glow_services_backend.model.entity.Admin;
import com.glowservices.glow_services_backend.repository.AdminRepository;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            // ✅ FIXED: Check if admin already exists before creating
            if (adminRepository.count() == 0) {
                createDefaultAdmin();
                System.out.println("✅ Default admin user created successfully");
            } else {
                System.out.println("ℹ️ Admin user already exists, skipping creation");
            }
        } catch (Exception e) {
            System.err.println("❌ Error initializing admin data: " + e.getMessage());
            // Don't throw exception to prevent application startup failure
        }
    }

    private void createDefaultAdmin() {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFullName("Glow Services Administrator");
        admin.setEmail("admin@glowservices.com");
        admin.setRole(Admin.AdminRole.ADMIN);
        admin.setActive(true);

        adminRepository.save(admin);
    }
}