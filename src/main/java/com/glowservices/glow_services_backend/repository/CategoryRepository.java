package com.glowservices.glow_services_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.glowservices.glow_services_backend.model.entity.Category;
import com.glowservices.glow_services_backend.model.entity.Category.CategoryType;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find by slug
    Optional<Category> findBySlug(String slug);

    // Find by name
    Optional<Category> findByName(String name);

    // Find active categories
    List<Category> findByActiveTrue();

    // Find by type and active status
    List<Category> findByTypeAndActiveTrue(CategoryType type);

    // Find active categories ordered by sort order
    List<Category> findByActiveTrueOrderBySortOrderAsc();

    // Find by type ordered by sort order
    List<Category> findByTypeOrderBySortOrderAsc(CategoryType type);

    // Check if slug exists
    boolean existsBySlug(String slug);

    // Check if name exists
    boolean existsByName(String name);

    // Check if slug exists excluding current category
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.slug = :slug AND c.id != :id")
    boolean existsBySlugAndIdNot(@Param("slug") String slug, @Param("id") Long id);

    // Find categories by name containing (for search)
    List<Category> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    // Count categories by type
    long countByTypeAndActiveTrue(CategoryType type);
}