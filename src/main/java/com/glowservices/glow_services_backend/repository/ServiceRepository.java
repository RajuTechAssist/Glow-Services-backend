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

    List<ServiceEntity> findByCategory(String category);
    
    List<ServiceEntity> findByCategoryAndActiveTrue(String category);

    List<ServiceEntity> findByPopularTrue();

    List<ServiceEntity> findByActiveTrue();

    // ✅ FIXED: Simple LIKE query (Wildcards will be passed from Service)
    @Query("SELECT s FROM ServiceEntity s WHERE s.active = true AND " +
           "(LOWER(s.name) LIKE LOWER(:searchTerm) OR " +
           "LOWER(s.description) LIKE LOWER(:searchTerm))")
    List<ServiceEntity> findBySearchTerm(@Param("searchTerm") String searchTerm);

    // ✅ FIXED: Simple LIKE query for Category + Search
    @Query("SELECT s FROM ServiceEntity s WHERE s.active = true AND s.category = :category AND " +
           "(LOWER(s.name) LIKE LOWER(:searchTerm) OR " +
           "LOWER(s.description) LIKE LOWER(:searchTerm))")
    List<ServiceEntity> findByCategoryAndSearchTerm(@Param("category") String category, @Param("searchTerm") String searchTerm);
}