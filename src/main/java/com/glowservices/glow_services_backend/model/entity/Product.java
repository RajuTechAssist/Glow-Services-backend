package com.glowservices.glow_services_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "product_code", length = 255)
    private String productCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description", length = 255)
    private String shortDescription;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(length = 100)
    private String subCategory;

    @Column(length = 100)
    private String brand;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "stockquantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold = 10;

    @Column(length = 50)
    private String size;

    @Column(length = 50)
    private String color;

    @Column(length = 255)
    private String sku;

    // ✅ FIXED: Added <String> type and proper initialization
    @ElementCollection
    @CollectionTable(name = "product_ingredients", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "ingredient")
    private List<String> ingredients = new ArrayList<>();

    // ✅ FIXED: Added <String> type and proper initialization
    @ElementCollection
    @CollectionTable(name = "product_how_to_use", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "step")
    private List<String> howToUse = new ArrayList<>();

    // ✅ FIXED: Added <String> type and proper initialization
    @ElementCollection
    @CollectionTable(name = "product_features", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "feature")
    private List<String> features = new ArrayList<>();

    // ✅ FIXED: Added <String> type and proper initialization
    @ElementCollection
    @CollectionTable(name = "product_benefits", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "benefit")
    private List<String> benefits = new ArrayList<>();

    // ✅ FIXED: Added <String> type and proper initialization
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", length = 500)
    private List<String> images = new ArrayList<>();

    // ✅ FIXED: Added <String> type and proper initialization
    @ElementCollection
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Column(precision = 3, scale = 2)
    private BigDecimal rating = new BigDecimal("5.0");

    @Column
    private Integer reviewCount = 0;

    @Column
    private Boolean featured = false;

    @Column
    private Boolean popular = false;

    @Column(name = "new_arrival")
    private Boolean newArrival = false;

    @Column(name = "on_sale")
    private Boolean onSale = false;

    @Column
    private Boolean active = true;

    @Column
    private Integer sortOrder = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
        
        // ✅ Initialize lists if null
        if (ingredients == null) ingredients = new ArrayList<>();
        if (howToUse == null) howToUse = new ArrayList<>();
        if (features == null) features = new ArrayList<>();
        if (benefits == null) benefits = new ArrayList<>();
        if (images == null) images = new ArrayList<>();
        if (tags == null) tags = new ArrayList<>();
        
        // Generate slug if not provided
        if (slug == null || slug.trim().isEmpty()) {
            slug = generateSlug(name);
        }
        
        // Set defaults
        if (active == null) active = true;
        if (featured == null) featured = false;
        if (popular == null) popular = false;
        if (newArrival == null) newArrival = false;
        if (onSale == null) onSale = false;
        if (stockQuantity == null) stockQuantity = 0;
        if (rating == null) rating = new BigDecimal("5.0");
        if (reviewCount == null) reviewCount = 0;
        if (sortOrder == null) sortOrder = 0;
        if (lowStockThreshold == null) lowStockThreshold = 10;
        
        // Calculate discount
        if (originalPrice != null && price != null) {
            discountPercentage = calculateDiscount(originalPrice, price);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        
        // Recalculate discount
        if (originalPrice != null && price != null) {
            discountPercentage = calculateDiscount(originalPrice, price);
        }
    }

    private String generateSlug(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        return name.toLowerCase()
                  .trim()
                  .replaceAll("[^a-z0-9\\s-]", "")
                  .replaceAll("\\s+", "-")
                  .replaceAll("-+", "-")
                  .replaceAll("^-|-$", "");
    }

    private Double calculateDiscount(BigDecimal original, BigDecimal current) {
        if (original.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return original.subtract(current)
                      .divide(original, 2, java.math.RoundingMode.HALF_UP)
                      .multiply(new BigDecimal("100"))
                      .doubleValue();
    }
}
