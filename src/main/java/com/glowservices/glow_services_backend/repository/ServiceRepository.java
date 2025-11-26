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

    // ✅ FIXED: This now correctly queries by the 'category' field, which should store the slug.
    List<ServiceEntity> findByCategory(String category);
    
    // ✅ FIXED: This now correctly queries by the 'category' field and active status.
    List<ServiceEntity> findByCategoryAndActiveTrue(String category);

    List<ServiceEntity> findByPopularTrue();

    List<ServiceEntity> findByActiveTrue();

    @Query("SELECT s FROM ServiceEntity s WHERE s.active = true AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<ServiceEntity> findBySearchTerm(@Param("searchTerm") String searchTerm);

    @Query("SELECT s FROM ServiceEntity s WHERE s.active = true AND s.category = :category AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<ServiceEntity> findByCategoryAndSearchTerm(@Param("category") String category, @Param("searchTerm") String searchTerm);
}