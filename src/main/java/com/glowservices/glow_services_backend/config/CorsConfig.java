// package com.glowservices.glow_services_backend.config;

// import java.util.Arrays;
// import java.util.List;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.filter.CorsFilter;

// @Configuration
// public class CorsConfig {
    
//     @Bean
//     public CorsFilter corsFilter() {
//         CorsConfiguration corsConfiguration = new CorsConfiguration();
//         corsConfiguration.setAllowCredentials(true);
//         corsConfiguration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173", "https://glow-services-frontend-15jfz525u-rajutechassists-projects.vercel.app"));
//         // corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
//         corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//         corsConfiguration.setExposedHeaders(List.of("Authorization", "Location"));
//         corsConfiguration.setAllowCredentials(true); // only if you plan to use cookies/credentials

//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", corsConfiguration);
//         return new CorsFilter(source);
//     }
// }


// package com.glowservices.glow_services_backend.config;

// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import java.util.Arrays;

// @Configuration
// public class CorsConfig {

//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration configuration = new CorsConfiguration();
        
//         // Set allowed origins
//         configuration.setAllowedOrigins(Arrays.asList(
//             "http://localhost:5173",
//             "http://localhost:3000",
//             "https://glow-service.studio",
//             "https://www.glow-service.studio"
//         ));
        
//         // Set allowed methods
//         configuration.setAllowedMethods(Arrays.asList(
//             "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
//         ));
        
//         // Set allowed headers
//         configuration.setAllowedHeaders(Arrays.asList(
//             "Content-Type", "Authorization", "X-Requested-With", "Accept"
//         ));
        
//         // Allow credentials
//         configuration.setAllowCredentials(true);
        
//         // Max age
//         configuration.setMaxAge(3600L);
        
//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", configuration);
//         return source;
//     }
// }
