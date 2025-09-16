package com.glowservices.glow_services_backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_category_slug", columnList = "slug"),
    @Index(name = "idx_category_type_active", columnList = "type, active"),
    @Index(name = "idx_category_active_sort", columnList = "active, sort_order")
})
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", sequenceName = "category_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    @Column(name = "slug", unique = true, nullable = false)
    @NotBlank(message = "Category slug is required")
    @Size(min = 2, max = 100, message = "Category slug must be between 2 and 100 characters")
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Column(name = "icon", length = 50)
    private String icon; // Emoji or icon class

    @Column(name = "color", length = 50)
    private String color; // CSS color class or hex code

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CategoryType type = CategoryType.SERVICE;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors for convenience
    public Category(String name, String description, CategoryType type) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.slug = generateSlugFromName(name);
    }

    public Category(String name, String slug, String description, CategoryType type, String icon) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.type = type;
        this.icon = icon;
    }

    // Helper method to generate slug (can be used in constructor)
    private String generateSlugFromName(String name) {
        return name.toLowerCase()
                  .trim()
                  .replaceAll("[^a-z0-9\\s-]", "")
                  .replaceAll("\\s+", "-")
                  .replaceAll("-+", "-")
                  .replaceAll("^-|-$", "");
    }

    public enum CategoryType {
        SERVICE("Service Category"),
        PRODUCT("Product Category"), 
        BOTH("Service & Product Category");

        private final String displayName;

        CategoryType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Override toString for better logging
    @Override
    public String toString() {
        return String.format("Category{id=%d, name='%s', slug='%s', type=%s, active=%s}", 
                            id, name, slug, type, active);
    }
}