package com.glowservices.glow_services_backend.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.glowservices.glow_services_backend.model.enums.BlogStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "blogs")
@AllArgsConstructor
@NoArgsConstructor
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(unique = true, length = 250)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String excerpt;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    private BlogStatus status = BlogStatus.DRAFT;

    @Column(nullable = false)
    private String author;

    private String featuredImage;

    @ElementCollection
    @CollectionTable(name = "blog_tags", joinColumns = @JoinColumn(name = "blog_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    private String metaTitle;
    private String metaDescription;
    private LocalDateTime publishDate;
    private boolean featured = false;

    private Long views = 0L;
    private Long likes = 0L;
    private Long comments = 0L;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    
}
