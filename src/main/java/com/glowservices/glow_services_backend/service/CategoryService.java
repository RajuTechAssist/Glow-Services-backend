package com.glowservices.glow_services_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.glowservices.glow_services_backend.model.entity.Category;
import com.glowservices.glow_services_backend.model.entity.Category.CategoryType;
import com.glowservices.glow_services_backend.repository.CategoryRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Get all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAsc();
    }

    // Get all categories including inactive
    public List<Category> getAllCategoriesIncludingInactive() {
        return categoryRepository.findAll();
    }

    // Get category by ID
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    // Get category by slug
    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
            .orElseThrow(() -> new RuntimeException("Category not found with slug: " + slug));
    }

    // Get categories by type
    public List<Category> getCategoriesByType(CategoryType type) {
        return categoryRepository.findByTypeAndActiveTrue(type);
    }

    // Get all categories by type (including inactive)
    public List<Category> getAllCategoriesByType(CategoryType type) {
        return categoryRepository.findByTypeOrderBySortOrderAsc(type);
    }

    // Search categories by name
    public List<Category> searchCategories(String searchTerm) {
        return categoryRepository.findByNameContainingIgnoreCaseAndActiveTrue(searchTerm);
    }

    // Create new category
    public Category createCategory(Category category) {
        // Validate required fields
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new RuntimeException("Category name is required");
        }

        // Generate slug if not provided
        if (category.getSlug() == null || category.getSlug().trim().isEmpty()) {
            category.setSlug(generateSlug(category.getName()));
        } else {
            category.setSlug(generateSlug(category.getSlug()));
        }

        // Check for duplicate slug
        if (categoryRepository.existsBySlug(category.getSlug())) {
            throw new RuntimeException("Category with slug '" + category.getSlug() + "' already exists");
        }

        // Check for duplicate name
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Category with name '" + category.getName() + "' already exists");
        }

        // Set timestamps
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        // Set default values if not provided
        if (category.getType() == null) {
            category.setType(CategoryType.SERVICE);
        }

        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }

        return categoryRepository.save(category);
    }

    // Update existing category
    public Category updateCategory(Long id, Category updatedCategory) {
        Category existingCategory = getCategoryById(id);

        // Update basic fields
        existingCategory.setName(updatedCategory.getName());
        existingCategory.setDescription(updatedCategory.getDescription());
        existingCategory.setIcon(updatedCategory.getIcon());
        existingCategory.setColor(updatedCategory.getColor());
        existingCategory.setType(updatedCategory.getType());
        existingCategory.setActive(updatedCategory.isActive());
        existingCategory.setSortOrder(updatedCategory.getSortOrder());
        existingCategory.setUpdatedAt(LocalDateTime.now());

        // Update slug if name changed
        String newSlug = generateSlug(updatedCategory.getName());
        if (!newSlug.equals(existingCategory.getSlug())) {
            // Check if new slug already exists (excluding current category)
            if (categoryRepository.existsBySlugAndIdNot(newSlug, id)) {
                throw new RuntimeException("Category with slug '" + newSlug + "' already exists");
            }
            existingCategory.setSlug(newSlug);
        }

        return categoryRepository.save(existingCategory);
    }

    // Delete category
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);

        // TODO: Check if category is being used by services/products
        // You might want to prevent deletion if category has associated items

        categoryRepository.delete(category);
    }

    // Soft delete (set active = false)
    public Category deactivateCategory(Long id) {
        Category category = getCategoryById(id);
        category.setActive(false);
        category.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    // Activate category
    public Category activateCategory(Long id) {
        Category category = getCategoryById(id);
        category.setActive(true);
        category.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    // Get category statistics
    public CategoryStats getCategoryStats() {
        long totalCategories = categoryRepository.count();
        long activeCategories = categoryRepository.findByActiveTrue().size();
        long serviceCategories = categoryRepository.countByTypeAndActiveTrue(CategoryType.SERVICE);
        long productCategories = categoryRepository.countByTypeAndActiveTrue(CategoryType.PRODUCT);
        long bothCategories = categoryRepository.countByTypeAndActiveTrue(CategoryType.BOTH);

        return new CategoryStats(totalCategories, activeCategories, serviceCategories, productCategories, bothCategories);
    }

    // Helper method to generate slug from name
    private String generateSlug(String name) {
        return name.toLowerCase()
                  .trim()
                  .replaceAll("[^a-z0-9\\s-]", "") // Remove special characters
                  .replaceAll("\\s+", "-") // Replace spaces with hyphens
                  .replaceAll("-+", "-") // Replace multiple hyphens with single
                  .replaceAll("^-|-$", ""); // Remove leading/trailing hyphens
    }

    // Inner class for category statistics
    public static class CategoryStats {
        private final long totalCategories;
        private final long activeCategories;
        private final long serviceCategories;
        private final long productCategories;
        private final long bothCategories;

        public CategoryStats(long totalCategories, long activeCategories, long serviceCategories, long productCategories, long bothCategories) {
            this.totalCategories = totalCategories;
            this.activeCategories = activeCategories;
            this.serviceCategories = serviceCategories;
            this.productCategories = productCategories;
            this.bothCategories = bothCategories;
        }

        // Getters
        public long getTotalCategories() { return totalCategories; }
        public long getActiveCategories() { return activeCategories; }
        public long getServiceCategories() { return serviceCategories; }
        public long getProductCategories() { return productCategories; }
        public long getBothCategories() { return bothCategories; }
    }
}