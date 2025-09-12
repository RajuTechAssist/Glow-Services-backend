package com.glowservices.glow_services_backend.model.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {
    private String name;
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
    private List<String> images;
    private List<String> features;
    private List<String> tags;
    private Boolean featured = false;
    private Boolean popular = false;
    private Boolean newArrival = false;
    private Boolean onSale = false;
    private Boolean active = true;
}