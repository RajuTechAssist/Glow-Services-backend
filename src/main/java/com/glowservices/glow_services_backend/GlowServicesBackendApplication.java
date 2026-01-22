// How to Run locally in the Future: cd glow-services-backend; .\mvnw spring-boot:run "-Dspring-boot.run.profiles=local" "-Daws.accessKeyId=dummy" "-Daws.secretKey=dummy"
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
            dotenv.entries().forEach(entry -> {
                setIfMissing(entry.getKey(), entry.getValue());
            });
            
            // Map standard AWS env vars to Java System Properties expected by AWS SDK
            // The SDK looks for "aws.accessKeyId" and "aws.secretKey" (or "aws.secretAccessKey")
            if (dotenv.get("AWS_ACCESS_KEY_ID") != null) {
                setIfMissing("aws.accessKeyId", dotenv.get("AWS_ACCESS_KEY_ID"));
            }
            if (dotenv.get("AWS_SECRET_ACCESS_KEY") != null) {
                setIfMissing("aws.secretAccessKey", dotenv.get("AWS_SECRET_ACCESS_KEY"));
                setIfMissing("aws.secretKey", dotenv.get("AWS_SECRET_ACCESS_KEY")); // Legacy support
            }
            if (dotenv.get("AWS_REGION") != null) {
                setIfMissing("aws.region", dotenv.get("AWS_REGION"));
            }

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
