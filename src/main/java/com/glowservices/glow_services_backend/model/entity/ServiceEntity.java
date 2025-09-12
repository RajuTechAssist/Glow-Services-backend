package com.glowservices.glow_services_backend.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
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
    private String category;

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
    private List<String> features; // Fixed: Added <String> generic

    @ElementCollection
    @CollectionTable(name = "service_benefits", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "benefit")
    private List<String> benefits; // Fixed: Added <String> generic

    @ElementCollection
    @CollectionTable(name = "service_includes", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "service_item")
    private List<String> services; // Fixed: Added <String> generic - For combo offers

    private String image;

    @ElementCollection
    @CollectionTable(name = "service_gallery", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "image_url")
    private List<String> gallery; // Fixed: Added <String> generic

    private String gradient;

    private Boolean popular = false;

    private Boolean active = true;

    private Double savings;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now(); // Fixed: Use LocalDateTime directly

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now(); // Fixed: Use LocalDateTime directly
}
