package com.glowservices.glow_services_backend.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glowservices.glow_services_backend.model.dto.request.LoginRequest;
import com.glowservices.glow_services_backend.model.entity.Admin;
import com.glowservices.glow_services_backend.repository.AdminRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AdminAuthController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ MAIN LOGIN ENDPOINT (POST)
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("✅ POST request received at /api/admin/login");
        System.out.println("📝 LoginRequest object: " + loginRequest);
        System.out.println("👤 Username: '" + loginRequest.getUsername() + "'");
        System.out.println("🔐 Password length: " + (loginRequest.getPassword() != null ? loginRequest.getPassword().length() : "null"));

        // Enhanced validation
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            System.out.println("❌ Username is null or empty");
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Username is required");
            error.put("status", "error");
            return ResponseEntity.status(400).body(error);
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            System.out.println("❌ Password is null or empty");
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Password is required");
            error.put("status", "error");
            return ResponseEntity.status(400).body(error);
        }

        try {
            String username = loginRequest.getUsername().trim();
            String password = loginRequest.getPassword().trim();

            System.out.println("🔍 Searching for admin with username: '" + username + "'");
            Optional<Admin> adminOpt = adminRepository.findByUsername(username);

            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                System.out.println("👤 Admin found: " + admin.getUsername());
                System.out.println("✅ Admin is active: " + admin.isActive());

                // Handle both plain text and encoded passwords
                boolean passwordMatch = password.equals(admin.getPassword()) || 
                                       passwordEncoder.matches(password, admin.getPassword());

                System.out.println("🔐 Password match: " + passwordMatch);

                if (passwordMatch && admin.isActive()) {
                    System.out.println("✅ Login successful for: " + admin.getUsername());

                    // Update last login
                    admin.setLastLogin(LocalDateTime.now());
                    adminRepository.save(admin);

                    // Create response
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", admin.getId());
                    response.put("username", admin.getUsername());
                    response.put("fullName", admin.getFullName());
                    response.put("email", admin.getEmail());
                    response.put("role", admin.getRole().toString());
                    response.put("message", "Login successful");
                    response.put("status", "success");

                    return ResponseEntity.ok(response);
                } else if (!admin.isActive()) {
                    System.out.println("❌ Admin account is inactive");
                } else {
                    System.out.println("❌ Password mismatch");
                }
            } else {
                System.out.println("❌ Admin not found for username: '" + username + "'");
            }

        } catch (Exception e) {
            System.out.println("💥 Login error: " + e.getMessage());
            e.printStackTrace();
        }

        // Return error response
        Map<String, Object> error = new HashMap<>();
        error.put("message", "Invalid username or password");
        error.put("status", "error");
        return ResponseEntity.status(401).body(error);
    }

    // 🚨 DEBUG GET ENDPOINT - This will help identify the issue
    @GetMapping("/login")
    public ResponseEntity<Map<String, Object>> loginGetDebug() {
        System.out.println("🚨 GET request received at /api/admin/login");
        System.out.println("⚠️  This indicates your frontend is making GET requests instead of POST!");
        System.out.println("🔧 Check your React form for missing preventDefault() or onSubmit handler");

        Map<String, Object> debugInfo = new HashMap<>();
        debugInfo.put("error", "Method Not Allowed");
        debugInfo.put("message", "Login endpoint requires POST request, but received GET");
        debugInfo.put("problem", "Your React form is using browser default submission (GET) instead of JavaScript fetch (POST)");
        debugInfo.put("solutions", Map.of(
            "1", "Add e.preventDefault() to your form submit handler",
            "2", "Ensure form has onSubmit={handleSubmit} attribute", 
            "3", "Make sure button type='submit' not type='button'",
            "4", "Check that fetch uses method: 'POST'"
        ));
        debugInfo.put("expectedRequest", Map.of(
            "method", "POST",
            "url", "/api/admin/login",
            "headers", Map.of("Content-Type", "application/json"),
            "body", Map.of("username", "admin", "password", "admin123")
        ));
        debugInfo.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(405).body(debugInfo);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        System.out.println("🔓 Logout request received");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    // Test endpoint
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin API is working");
        response.put("status", "success");
        response.put("timestamp", LocalDateTime.now());
        response.put("endpoints", Map.of(
            "login", "POST /api/admin/login",
            "logout", "POST /api/admin/logout",
            "test", "GET /api/admin/test"
        ));
        return ResponseEntity.ok(response);
    }
}