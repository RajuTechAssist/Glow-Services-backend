package com.glowservices.glow_services_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.glowservices.glow_services_backend.model.entity.ServiceEntity;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    Optional<ServiceEntity> findBySlug(String slug);

    // Find by Category Slug (e.g., 'facial', 'waxing')
    List<ServiceEntity> findByCategory(String category);
    
    // Find Active Services by Category
    List<ServiceEntity> findByCategoryAndActiveTrue(String category);

    List<ServiceEntity> findByPopularTrue();

    List<ServiceEntity> findByActiveTrue();

    // ✅ FIX: Search Query with Case-Insensitive Partial Match
    @Query("SELECT s FROM ServiceEntity s WHERE s.active = true AND " +
           "(LOWER(s.name) LIKE LOWER(:searchTerm) OR " +
           "LOWER(s.description) LIKE LOWER(:searchTerm))")
    List<ServiceEntity> findBySearchTerm(@Param("searchTerm") String searchTerm);

    // ✅ FIX: Category + Search Query
    @Query("SELECT s FROM ServiceEntity s WHERE s.active = true AND s.category = :category AND " +
           "(LOWER(s.name) LIKE LOWER(:searchTerm) OR " +
           "LOWER(s.description) LIKE LOWER(:searchTerm))")
    List<ServiceEntity> findByCategoryAndSearchTerm(@Param("category") String category, @Param("searchTerm") String searchTerm);
}