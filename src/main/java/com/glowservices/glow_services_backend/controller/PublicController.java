package com.glowservices.glow_services_backend.controller;

import com.glowservices.glow_services_backend.model.entity.Category;
import com.glowservices.glow_services_backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = {"http://localhost:5173", "https://glow-services-frontend.vercel.app/"}, allowCredentials = "true")
public class PublicController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getActiveCategories() {
        // This reuses your existing service method to get only active categories
        List<Category> categories = categoryService.getAllCategories(); 
        return ResponseEntity.ok(categories);
    }
}