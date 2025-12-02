package com.glowservices.glow_services_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.glowservices.glow_services_backend.security.JwtAuthenticationFilter;
import com.glowservices.glow_services_backend.security.JwtTokenProvider;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private JwtTokenProvider tokenProvider;

        @Autowired
    private CorsConfigurationSource corsConfigurationSource; // Inject the Bean from CorsConfig

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // 1. ENABLE CORS in Security and use our custom source
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                        // 2. CRITICAL: Allow all OPTIONS (Preflight) requests globally
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                
                                                // Public Endpoints
                                                .requestMatchers("/api/admin/login", "/api/admin/logout").permitAll()
                                                .requestMatchers("/api/admin/ai/**").permitAll()
                                                .requestMatchers("/api/admin/**").authenticated()
                                          
                                                .requestMatchers("/api/services/**").permitAll()
                                                .requestMatchers("/api/products/**").permitAll()
                                                .requestMatchers("/api/categories/**").permitAll()
                                                .requestMatchers("/api/customers/**").permitAll()
                                                .requestMatchers("/api/contact/**").permitAll()
                                                .requestMatchers("/api/newsletter/**").permitAll()
                                                .requestMatchers("/api/public/**").permitAll()
                                                .requestMatchers("/api/health/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/admin/blogs/published")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/admin/blogs/{id}").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/admin/blogs/featured")
                                                .permitAll()

                                                .requestMatchers("/api/public/**").permitAll()
                                                // Allow actuator health check
                                                .requestMatchers("/actuator/health/**").permitAll()
                                                // Require authentication for admin endpoints
                                                .requestMatchers("/api/auth/**").permitAll()
                                                // Allow all other requests for now (we'll secure them later)
                                                .anyRequest().permitAll())
                                // ✅ Add the JWT filter before the standard username/password filter
                                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
                                                UsernamePasswordAuthenticationFilter.class);

                // .formLogin(form -> form
                // .loginPage("/admin/login")
                // .defaultSuccessUrl("/admin/dashboard", true)
                // .permitAll())
                // .logout(logout -> logout
                // .logoutUrl("/admin/logout")
                // .logoutSuccessUrl("/"));
                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        
}
