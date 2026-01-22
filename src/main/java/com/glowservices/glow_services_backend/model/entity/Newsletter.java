package com.glowservices.glow_services_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "newsletter_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Newsletter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @CreationTimestamp
    @Column(name = "subscribed_at", updatable = false)
    private LocalDateTime subscribedAt;
    
    private boolean active = true;
}
