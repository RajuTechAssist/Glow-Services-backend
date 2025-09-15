package com.glowservices.glow_services_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.glowservices.glow_services_backend.model.entity.Admin;
import com.glowservices.glow_services_backend.repository.AdminRepository;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Arrays;

@SpringBootApplication
public class GlowServicesBackendApplication implements org.springframework.boot.CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure().filename(".env").load();
        System.setProperty("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL"));
        System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));

        SpringApplication.run(GlowServicesBackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if not exists
        if (!adminRepository.existsByUsername("admin")) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword("admin123"); // Store as plain text for demo
            admin.setEmail("admin@glowservices.com");
            admin.setFullName("System Administrator");
            admin.setActive(true);

            adminRepository.save(admin);

            System.out.println("✅ Default admin user created:");
            System.out.println("   Username: admin");
            System.out.println("   Password: admin123");
            System.out.println("   Email: admin@glowservices.com");
        } else {
            System.out.println("ℹ️ Admin user already exists");
        }
    }

    // @Bean
    // public CorsFilter corsFilter() {
    // CorsConfiguration corsConfiguration = new CorsConfiguration();
    // corsConfiguration.setAllowCredentials(true);
    // corsConfiguration.setAllowedOriginPatterns(Arrays.asList("*"));
    // corsConfiguration.setAllowedHeaders(Arrays.asList("Origin",
    // "Access-Control-Allow-Origin",
    // "Content-Type", "Accept", "Authorization", "Origin, Accept",
    // "X-Requested-With",
    // "Access-Control-Request-Method", "Access-Control-Request-Headers"));
    // corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type",
    // "Accept",
    // "Authorization", "Access-Control-Allow-Origin",
    // "Access-Control-Allow-Origin",
    // "Access-Control-Allow-Credentials"));
    // corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT",
    // "DELETE", "OPTIONS"));

    // UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new
    // UrlBasedCorsConfigurationSource();
    // urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",
    // corsConfiguration);

    // return new CorsFilter(urlBasedCorsConfigurationSource);
    // }
}
