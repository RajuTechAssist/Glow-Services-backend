package com.glowservices.glow_services_backend.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String category; // Category slug reference

    @Column(nullable = false)
    private Double price;

    private Double originalPrice;

    @Column(nullable = false)
    private String duration;

    private Double rating = 5.0;
    private Integer reviews = 0;

    @Column(length = 500)
    private String description;

    @Column(length = 1500)
    private String longDescription;

    @ElementCollection
    @CollectionTable(name = "service_features", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "feature")
    private List<String> features;

    @ElementCollection
    @CollectionTable(name = "service_benefits", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "benefit")
    private List<String> benefits;

    @ElementCollection
    @CollectionTable(name = "service_includes", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "service_item")
    private List<String> services;

    private String image;

    @ElementCollection
    @CollectionTable(name = "service_gallery", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "image_url")
    private List<String> gallery;

    private String gradient;
    private Boolean popular = false;
    private Boolean active = true;
    private Double savings;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Auto-generate slug before saving
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
        
        // Generate slug if not provided
        if (this.slug == null || this.slug.trim().isEmpty()) {
            this.slug = generateSlug(this.name);
        }
        
        // Calculate savings if not provided
        if (this.savings == null && this.originalPrice != null) {
            this.savings = this.originalPrice - this.price;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        
        // Recalculate savings
        if (this.originalPrice != null) {
            this.savings = this.originalPrice - this.price;
        }
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
