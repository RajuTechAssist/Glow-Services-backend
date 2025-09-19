package com.glowservices.glow_services_backend.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = {"http://localhost:5173", "https://glow-services-frontend.vercel.app"})
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/database")
    public ResponseEntity<?> checkDatabaseHealth() {
        Map<String, Object> health = new HashMap<>();

        try {
            Connection connection = dataSource.getConnection();
            boolean isValid = connection.isValid(5); // 5 second timeout
            connection.close();

            health.put("database", "UP");
            health.put("connection_valid", isValid);
            health.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(health);
        } catch (SQLException e) {
            health.put("database", "DOWN");
            health.put("error", e.getMessage());
            health.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(health);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getAppStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("application", "Glow Services Backend");
        status.put("status", "UP");
        status.put("version", "1.0.0");
        status.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(status);
    }
}
