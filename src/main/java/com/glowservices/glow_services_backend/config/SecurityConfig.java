package com.glowservices.glow_services_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/admin/login", "/api/admin/logout").permitAll()
                                .requestMatchers("/api/admin/**").authenticated()
                                                // Allow public access to these endpoints
                                                .requestMatchers("/api/services/**").permitAll()
                                                .requestMatchers("/api/products/**").permitAll()
                                                .requestMatchers("/api/categories/**").permitAll()
                                                .requestMatchers("/api/customers/**").permitAll()
                                                .requestMatchers("/api/contact/**").permitAll()
                                                .requestMatchers("/api/newsletter/**").permitAll()
                                                .requestMatchers("/api/public/**").permitAll()
                                                .requestMatchers("/api/health/**").permitAll()

                                                .requestMatchers("/api/public/**").permitAll()
                                                // Allow actuator health check
                                                .requestMatchers("/actuator/health/**").permitAll()
                                                // Require authentication for admin endpoints
                                                .requestMatchers("/api/auth/**").permitAll()
                                                // Allow all other requests for now (we'll secure them later)
                                                .anyRequest().permitAll())
                                                // ✅ Add the JWT filter before the standard username/password filter
                                                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
                                                
                                // .formLogin(form -> form
                                //                 .loginPage("/admin/login")
                                //                 .defaultSuccessUrl("/admin/dashboard", true)
                                //                 .permitAll())
                                // .logout(logout -> logout
                                //                 .logoutUrl("/admin/logout")
                                //                 .logoutSuccessUrl("/"));
                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Allow specific origins
                configuration.setAllowedOriginPatterns(
                                Arrays.asList("http://localhost:5173", "https://glow-service.studio", "https://www.glow-service.studio"));
                configuration.setAllowedOriginPatterns(Arrays.asList("*"));
                configuration.setAllowCredentials(true);

                // Allow common headers
                configuration.setAllowedHeaders(Arrays.asList(
                                "Origin",
                                "Content-Type",
                                "Accept",
                                "Authorization",
                                "X-Requested-With",
                                "Access-Control-Request-Method",
                                "Access-Control-Request-Headers"));

                // Allow common methods
                configuration.setAllowedMethods(Arrays.asList(
                                "GET",
                                "POST",
                                "PUT",
                                "DELETE",
                                "OPTIONS"));

                // Expose headers
                configuration.setExposedHeaders(Arrays.asList(
                        "Authorization", 
                        "Location"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }
}
