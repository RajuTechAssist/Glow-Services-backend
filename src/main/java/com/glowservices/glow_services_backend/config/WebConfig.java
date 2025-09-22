package com.glowservices.glow_services_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
          .addMapping("/api/**")
          .allowedOrigins("http://localhost:5173", "https://glow-services-frontend.vercel.app/")
          .allowedMethods("GET", "POST", "PUT", "DELETE")
          .allowCredentials(true);
    }
}
