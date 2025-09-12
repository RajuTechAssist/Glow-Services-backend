package com.glowservices.glow_services_backend.repository;

import com.glowservices.glow_services_backend.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySlug(String slug);

    List<Product> findByActiveTrue();

    List<Product> findByActiveTrueOrderByCreatedAtDesc();

    List<Product> findByCategoryAndActiveTrue(String category);

    List<Product> findBySubCategoryAndActiveTrue(String subCategory);

    List<Product> findByBrandAndActiveTrue(String brand);

    List<Product> findByFeaturedTrueAndActiveTrue();

    List<Product> findByPopularTrueAndActiveTrue();

    List<Product> findByNewArrivalTrueAndActiveTrue();

    List<Product> findByOnSaleTrueAndActiveTrue();

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Product> searchProducts(@Param("search") String search);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.category = :category AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Product> searchProductsByCategory(@Param("search") String search, @Param("category") String category);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true ORDER BY p.category")
    List<String> findAllActiveCategories();

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.active = true ORDER BY p.brand")
    List<String> findAllActiveBrands();

    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.rating DESC, p.reviewCount DESC")
    List<Product> findTopRatedProducts();

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findProductsByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
}