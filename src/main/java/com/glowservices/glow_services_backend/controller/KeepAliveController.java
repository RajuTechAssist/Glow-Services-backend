package com.glowservices.glow_services_backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
// @CrossOrigin(origins = "https://glow-services-frontend.vercel.app/") 
public class KeepAliveController {

    @GetMapping("/keep-alive")
    public ResponseEntity<Map<String, String>> keepAlive() {
        // You can add logging or DB pings here if needed
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}