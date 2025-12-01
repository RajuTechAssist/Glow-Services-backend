package com.glowservices.glow_services_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedOrigins(
                "http://localhost:5173",
                "http://localhost:3000",
                "https://glow-service.studio",
                "https://glow-services-frontend-15jfz525u-rajutechassists-projects.vercel.app",
                "https://www.glow-service.studio"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("Authorization")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
