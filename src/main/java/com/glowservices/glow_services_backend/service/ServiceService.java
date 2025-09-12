package com.glowservices.glow_services_backend.service;

import com.glowservices.glow_services_backend.model.entity.ServiceEntity;
import com.glowservices.glow_services_backend.repository.ServiceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceService {
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    public List<ServiceEntity> getAllActiveServices() {
        return serviceRepository.findByActiveTrue();
    } // Fixed: Added missing closing brace
    
    public Optional<ServiceEntity> getServiceBySlug(String slug) {
        return serviceRepository.findBySlug(slug);
    } // Fixed: Added missing closing brace
    
    public List<ServiceEntity> getServicesByCategory(String category) {
        if ("all".equals(category)) {
            return getAllActiveServices();
        }
        return serviceRepository.findByCategory(category);
    } // Fixed: Added missing closing brace
    
    public List<ServiceEntity> getFeaturedServices() {
        return serviceRepository.findByPopularTrue();
    } // Fixed: Added missing closing brace
    
    public List<ServiceEntity> searchServices(String searchTerm, String category, String sortBy) {
        List<ServiceEntity> services;
        
        if ("all".equals(category)) {
            services = serviceRepository.findBySearchTerm(searchTerm);
        } else {
            services = serviceRepository.findByCategoryAndSearchTerm(category, searchTerm);
        }
        
        // Apply sorting
        return sortServices(services, sortBy);
    } // Fixed: Added missing closing brace
    
    private List<ServiceEntity> sortServices(List<ServiceEntity> services, String sortBy) {
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
                    .sorted((s1, s2) -> Double.compare(s2.getRating(), s1.getRating()))
                    .toList();
            case "popular":
            default:
                return services.stream()
                    .sorted((s1, s2) -> Boolean.compare(s2.getPopular(), s1.getPopular()))
                    .toList();
        }
    } // Fixed: Added missing closing brace
    
    public ServiceEntity createService(ServiceEntity service) {
        service.setCreatedAt(LocalDateTime.now());
        service.setUpdatedAt(LocalDateTime.now());
        return serviceRepository.save(service);
    } // Fixed: Added missing closing brace
    
    public Optional<ServiceEntity> updateService(Long id, ServiceEntity serviceDetails) {
        return serviceRepository.findById(id)
            .map(service -> {
                service.setName(serviceDetails.getName());
                service.setSlug(serviceDetails.getSlug());
                service.setCategory(serviceDetails.getCategory());
                service.setPrice(serviceDetails.getPrice());
                service.setOriginalPrice(serviceDetails.getOriginalPrice());
                service.setDescription(serviceDetails.getDescription());
                service.setLongDescription(serviceDetails.getLongDescription());
                service.setFeatures(serviceDetails.getFeatures());
                service.setBenefits(serviceDetails.getBenefits());
                service.setServices(serviceDetails.getServices());
                service.setPopular(serviceDetails.getPopular());
                service.setActive(serviceDetails.getActive());
                service.setUpdatedAt(LocalDateTime.now());
                return serviceRepository.save(service);
            });
    } // Fixed: Added missing closing brace
    
    public boolean deleteService(Long id) {
        return serviceRepository.findById(id)
            .map(service -> {
                service.setActive(false); // Soft delete
                serviceRepository.save(service);
                return true;
            })
            .orElse(false);
    } // Fixed: Added missing closing brace
} // Fixed: Added final closing brace
