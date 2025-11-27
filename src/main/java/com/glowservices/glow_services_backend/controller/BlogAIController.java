package com.glowservices.glow_services_backend.controller;

import com.glowservices.glow_services_backend.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/ai")
@CrossOrigin(origins = {"http://localhost:5173", "https://glow-service.studio", "https://www.glow-service.studio"}, allowCredentials = "true")
public class BlogAIController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateBlog(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        String tone = request.getOrDefault("tone", "professional");
        
        String generatedContent = geminiService.generateBlogContent(topic, tone);
        
        return ResponseEntity.ok(Map.of("content", generatedContent));
    }
}