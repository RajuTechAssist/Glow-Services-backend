package com.glowservices.glow_services_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;


import com.glowservices.glow_services_backend.model.entity.Admin;
import com.glowservices.glow_services_backend.repository.AdminRepository;

import io.github.cdimascio.dotenv.Dotenv;


@SpringBootApplication
public class GlowServicesBackendApplication {


    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {

        // Load .env if present, but don't fail if it's missing (Render doesn't use a
        // .env file)
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing() // <-- important: prevents exception when no .env
                    .load();

            // set System properties only if not already present as env vars or system props
            setIfMissing("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL"));
            setIfMissing("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
            setIfMissing("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));

            setIfMissing("spring.datasource.driver-class-name", dotenv.get("SPRING_DATASOURCE_DRIVER"));
            // add any other keys you used from .env similarly
        } catch (Exception ex) {
            // ignore: if any error occurs, fall back to environment variables /
            // application.properties
        }

        SpringApplication.run(GlowServicesBackendApplication.class, args);
    }

    private static void setIfMissing(String key, String value) {
        if (value == null || value.isBlank())
            return;
        // prefer real environment variables (Render), then existing system props, then
        // set
        if (System.getenv(key) == null && System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }
}
