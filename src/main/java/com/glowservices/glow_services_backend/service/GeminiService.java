package com.glowservices.glow_services_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String generateBlogContent(String topic, String tone) {
        RestTemplate restTemplate = new RestTemplate();
        String url = apiUrl + "?key=" + apiKey;

        // 1. Construct the Prompt
        String promptText = "Write a detailed, professional beauty blog post about: '" + topic + "'. " +
                            "Tone: " + tone + ". " +
                            "Use Markdown formatting. Include headings (##), bullet points, and a conclusion.";

        // 2. Build JSON Body (Gemini Structure)
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> parts = new HashMap<>();
        parts.put("text", promptText);
        content.put("parts", List.of(parts));
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        // 3. Send Request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            
            // 4. Parse Response (Deeply nested JSON)
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            Map<String, Object> contentResponse = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> partsResponse = (List<Map<String, Object>>) contentResponse.get("parts");
            
            return (String) partsResponse.get(0).get("text");

        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating content: " + e.getMessage();
        }
    }
}