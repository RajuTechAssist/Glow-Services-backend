package com.glowservices.glow_services_backend.controller;

import com.glowservices.glow_services_backend.model.entity.ServiceEntity;
import com.glowservices.glow_services_backend.service.ServiceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

   @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServiceEntity>> getAllServices(
            @RequestParam(required = false, defaultValue = "all") String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "popular") String sortBy) {

        try {
            List<ServiceEntity> services;

            // ✅ FIX: If search is present, use searchServices method
            if (search != null && !search.trim().isEmpty()) {
                services = serviceService.searchServices(search.trim(), category, sortBy);
                System.out.println("🔍 Searching for: " + search + " | Found: " + services.size());
            } 
            // ✅ FIX: If no search, but category is specific, filter by category
            else if (!"all".equalsIgnoreCase(category)) {
                services = serviceService.getActiveServices(category);
                System.out.println("📂 Category filter: " + category + " | Found: " + services.size());
            } 
            // ✅ FIX: Otherwise, get all active services
            else {
                services = serviceService.getActiveServices("all");
                System.out.println("🌟 Fetching all active services | Found: " + services.size());
            }

            return ResponseEntity.ok(services);

        } catch (Exception e) {
            System.err.println("❌ Error in getAllServices: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServiceEntity> getServiceBySlug(@PathVariable String slug) {
        try {
            System.out.println("Getting service by slug: " + slug);
            return serviceService.getServiceBySlug(slug)
                    .map(service -> {
                        System.out.println("Found service: " + service.getName());
                        return ResponseEntity.ok().body(service);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error in getServiceBySlug: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/featured", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServiceEntity>> getFeaturedServices() {
        try {
            System.out.println("Getting featured services");
            List<ServiceEntity> services = serviceService.getFeaturedServices();
            System.out.println("Found " + services.size() + " featured services");
            return ResponseEntity.ok(services);
        } catch (Exception e) {
            System.err.println("Error in getFeaturedServices: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Test endpoint to check if controller is working
    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> test() {
        System.out.println("Test endpoint called");
        return ResponseEntity.ok("API is working from port 8081!");
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServiceEntity> createService(@RequestBody ServiceEntity service) {
        try {
            ServiceEntity createdService = serviceService.createService(service);
            return ResponseEntity.ok(createdService);
        } catch (Exception e) {
            System.err.println("Error creating service: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
