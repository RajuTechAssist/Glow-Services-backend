package com.glowservices.glow_services_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
            // Empty - CORS is now handled by SecurityConfig + CorsConfig
}
