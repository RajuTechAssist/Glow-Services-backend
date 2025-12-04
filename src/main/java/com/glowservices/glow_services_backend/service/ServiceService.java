package com.glowservices.glow_services_backend.service;

import com.glowservices.glow_services_backend.model.entity.ServiceEntity;
import com.glowservices.glow_services_backend.repository.ServiceRepository;
import com.glowservices.glow_services_backend.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // ✅ UPDATE: Add sortBy parameter
    public List<ServiceEntity> getActiveServices(String category, String sortBy) {
        List<ServiceEntity> services;
        
        if (category == null || "all".equalsIgnoreCase(category)) {
            services = serviceRepository.findByActiveTrue();
        } else {
            services = serviceRepository.findByCategoryAndActiveTrue(category);
        }
        
        // ✅ Apply sorting here too!
        return sortServices(services, sortBy);
    }

    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }

    public List<ServiceEntity> getAllServicesAdmin(String category) {
        if (category == null || "all".equalsIgnoreCase(category)) {
            return serviceRepository.findAll();
        }
        return serviceRepository.findByCategory(category);
    }

    public Optional<ServiceEntity> getServiceBySlug(String slug) {
        return serviceRepository.findBySlug(slug);
    }

    public List<ServiceEntity> getFeaturedServices() {
        return serviceRepository.findByPopularTrue();
    }

    public List<ServiceEntity> searchServices(String searchTerm, String category, String sortBy) {
        List<ServiceEntity> services;

        // ✅ FIX: Wrap search term in wildcards here for reliable partial matching
        String searchPattern = "%" + searchTerm + "%";

        // 1. Fetch results based on search & category
        if (category == null || "all".equalsIgnoreCase(category)) {
            services = serviceRepository.findBySearchTerm(searchPattern);
        } else {
            services = serviceRepository.findByCategoryAndSearchTerm(category, searchPattern);
        }

        // 2. Apply Sorting
        return sortServices(services, sortBy);
    }

    private List<ServiceEntity> sortServices(List<ServiceEntity> services, String sortBy) {
        if (sortBy == null) return services;

        switch (sortBy) {
            case "price-low":
                return services.stream()
                        .sorted((s1, s2) -> Double.compare(s1.getPrice(), s2.getPrice()))
                        .toList();
            case "price-high":
                return services.stream()
                        .sorted((s1, s2) -> Double.compare(s2.getPrice(), s1.getPrice()))
                        .toList();
            case "rating":
                return services.stream()
                        .sorted((s1, s2) -> Double.compare(
                            s2.getRating() != null ? s2.getRating() : 0.0, 
                            s1.getRating() != null ? s1.getRating() : 0.0
                        ))
                        .toList();
            case "newest":
                 return services.stream()
                        .sorted((s1, s2) -> s2.getCreatedAt().compareTo(s1.getCreatedAt()))
                        .toList();
            case "popular":
            default:
                return services.stream()
                        .sorted((s1, s2) -> Boolean.compare(
                            s2.getPopular() != null ? s2.getPopular() : false, 
                            s1.getPopular() != null ? s1.getPopular() : false
                        ))
                        .toList();
        }
    }

    public ServiceEntity createService(ServiceEntity service) {
        // Validate required fields
        if (service.getName() == null || service.getName().trim().isEmpty()) {
            throw new RuntimeException("Service name is required");
        }

        if (service.getCategory() == null || service.getCategory().trim().isEmpty()) {
            throw new RuntimeException("Service category is required");
        }

        if (service.getPrice() == null || service.getPrice() <= 0) {
            throw new RuntimeException("Service price must be greater than 0");
        }

        if (service.getDuration() == null || service.getDuration().trim().isEmpty()) {
            throw new RuntimeException("Service duration is required");
        }

        // Validate category exists
        validateCategory(service.getCategory());

        // Initialize lists if null
        if (service.getFeatures() == null) {
            service.setFeatures(new ArrayList<>());
        }
        if (service.getBenefits() == null) {
            service.setBenefits(new ArrayList<>());
        }
        if (service.getServices() == null) {
            service.setServices(new ArrayList<>());
        }
        if (service.getGallery() == null) {
            service.setGallery(new ArrayList<>());
        }

        // Generate slug if not provided
        if (service.getSlug() == null || service.getSlug().trim().isEmpty()) {
            service.setSlug(generateSlug(service.getName()));
        }

        // Check for duplicate slug
        if (serviceRepository.findBySlug(service.getSlug()).isPresent()) {
            throw new RuntimeException("Service with slug '" + service.getSlug() + "' already exists");
        }

        // Set timestamps
        service.setCreatedAt(LocalDateTime.now());
        service.setUpdatedAt(LocalDateTime.now());

        // Set default values
        if (service.getActive() == null) {
            service.setActive(true);
        }
        if (service.getPopular() == null) {
            service.setPopular(false);
        }
        if (service.getRating() == null) {
            service.setRating(5.0);
        }
        if (service.getReviews() == null) {
            service.setReviews(0);
        }

        // Calculate savings
        if (service.getOriginalPrice() != null && service.getOriginalPrice() > service.getPrice()) {
            service.setSavings(service.getOriginalPrice() - service.getPrice());
        }

        return serviceRepository.save(service);
    }

    public Optional<ServiceEntity> updateService(Long id, ServiceEntity serviceDetails) {
        return serviceRepository.findById(id)
                .map(service -> {
                    // Validate category if changed
                    if (serviceDetails.getCategory() != null &&
                            !serviceDetails.getCategory().equals(service.getCategory())) {
                        validateCategory(serviceDetails.getCategory());
                    }

                    // Update fields
                    if (serviceDetails.getName() != null) {
                        service.setName(serviceDetails.getName());
                    }
                    if (serviceDetails.getSlug() != null) {
                        service.setSlug(serviceDetails.getSlug());
                    }
                    if (serviceDetails.getCategory() != null) {
                        service.setCategory(serviceDetails.getCategory());
                    }
                    if (serviceDetails.getPrice() != null) {
                        service.setPrice(serviceDetails.getPrice());
                    }
                    if (serviceDetails.getOriginalPrice() != null) {
                        service.setOriginalPrice(serviceDetails.getOriginalPrice());
                    }
                    if (serviceDetails.getDuration() != null) {
                        service.setDuration(serviceDetails.getDuration());
                    }
                    if (serviceDetails.getDescription() != null) {
                        service.setDescription(serviceDetails.getDescription());
                    }
                    if (serviceDetails.getLongDescription() != null) {
                        service.setLongDescription(serviceDetails.getLongDescription());
                    }
                    if (serviceDetails.getFeatures() != null) {
                        service.setFeatures(serviceDetails.getFeatures());
                    }
                    if (serviceDetails.getBenefits() != null) {
                        service.setBenefits(serviceDetails.getBenefits());
                    }
                    if (serviceDetails.getServices() != null) {
                        service.setServices(serviceDetails.getServices());
                    }
                    if (serviceDetails.getImage() != null) {
                        service.setImage(serviceDetails.getImage());
                    }
                    if (serviceDetails.getGallery() != null) {
                        service.setGallery(serviceDetails.getGallery());
                    }
                    if (serviceDetails.getGradient() != null) {
                        service.setGradient(serviceDetails.getGradient());
                    }
                    if (serviceDetails.getPopular() != null) {
                        service.setPopular(serviceDetails.getPopular());
                    }
                    if (serviceDetails.getActive() != null) {
                        service.setActive(serviceDetails.getActive());
                    }

                    // Recalculate savings
                    if (service.getOriginalPrice() != null && service.getPrice() != null) {
                        service.setSavings(service.getOriginalPrice() - service.getPrice());
                    }

                    service.setUpdatedAt(LocalDateTime.now());
                    return serviceRepository.save(service);
                });
    }

    // ✅ NEW: Actually deletes the record from the database
    public boolean deleteService(Long id) {
        if (serviceRepository.existsById(id)) {
            serviceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean deleteServiceBySlug(String slug) {
        return serviceRepository.findBySlug(slug)
                .map(service -> {
                    service.setActive(false); // Soft delete
                    service.setUpdatedAt(LocalDateTime.now());
                    serviceRepository.save(service);
                    return true;
                })
                .orElse(false);
    }

    // Helper method to validate category exists
    private void validateCategory(String categorySlug) {
        categoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new RuntimeException("Category with slug '" + categorySlug + "' does not exist"));
    }

    // Helper method to generate slug
    private String generateSlug(String name) {
        return name.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
