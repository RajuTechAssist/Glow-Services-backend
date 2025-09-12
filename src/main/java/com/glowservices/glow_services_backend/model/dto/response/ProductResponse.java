package com.glowservices.glow_services_backend.model.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String category;
    private String subCategory;
    private String brand;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private String size;
    private String color;
    private String ingredients;
    private String howToUse;
    private BigDecimal rating;
    private Integer reviewCount;
    private List<String> images;
    private List<String> features;
    private List<String> tags;
    private Boolean featured;
    private Boolean popular;
    private Boolean newArrival;
    private Boolean onSale;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed fields
    private Boolean hasDiscount;
    private BigDecimal discountPercentage;
    private Boolean inStock;

    public Boolean getHasDiscount() {
        return originalPrice != null && originalPrice.compareTo(price) > 0;
    }

    public BigDecimal getDiscountPercentage() {
        if (originalPrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            return originalPrice.subtract(price)
                    .divide(originalPrice, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }

    public Boolean getInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }
}