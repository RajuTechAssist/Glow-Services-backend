package com.glowservices.glow_services_backend.controller;

import com.glowservices.glow_services_backend.model.entity.Newsletter;
import com.glowservices.glow_services_backend.service.NewsletterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
public class NewsletterController {

    private final NewsletterService newsletterService;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        
        try {
            Newsletter newsletter = newsletterService.subscribe(email);
            return ResponseEntity.ok(Map.of("message", "Subscribed successfully", "id", newsletter.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
