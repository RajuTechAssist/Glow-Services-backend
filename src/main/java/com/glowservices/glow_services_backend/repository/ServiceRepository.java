package com.glowservices.glow_services_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Fixed: Correct Param import
import org.springframework.stereotype.Repository;

import com.glowservices.glow_services_backend.model.entity.ServiceEntity; // Fixed: Use ServiceEntity

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> { // Fixed: Added generics
    
    Optional<ServiceEntity> findBySlug(String slug); // Fixed: Return ServiceEntity
    
    List<ServiceEntity> findByCategory(String category); // Fixed: Return ServiceEntity
    
    List<ServiceEntity> findByPopularTrue(); // Fixed: Return ServiceEntity
    
    List<ServiceEntity> findByActiveTrue(); // Fixed: Return ServiceEntity
    
       List<ServiceEntity> findByCategoryAndActiveTrue(String category);

    @Query("SELECT s FROM ServiceEntity s WHERE s.active = true AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<ServiceEntity> findBySearchTerm(@Param("searchTerm") String searchTerm); // Fixed: ServiceEntity
    
    @Query("SELECT s FROM ServiceEntity s WHERE s.active = true AND s.category = :category AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<ServiceEntity> findByCategoryAndSearchTerm(@Param("category") String category, @Param("searchTerm") String searchTerm); // Fixed: ServiceEntity
}
