package com.glowservices.glow_services_backend.service;

import com.glowservices.glow_services_backend.model.dto.request.ProductRequest;
import com.glowservices.glow_services_backend.model.dto.response.ProductResponse;
import com.glowservices.glow_services_backend.model.entity.Product;
import com.glowservices.glow_services_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    // Get all active products
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all active products");
        List<Product> products = productRepository.findByActiveTrue();
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get products with filtering and sorting
    public List<ProductResponse> getProducts(String category, String search, String sortBy) {
        log.info("Fetching products with filters - category: {}, search: {}, sortBy: {}",
                category, search, sortBy);

        List<Product> products;

        // Apply filters
        if (StringUtils.hasText(search) && StringUtils.hasText(category) && !category.equals("all")) {
            products = productRepository.searchProductsByCategory(search, category);
        } else if (StringUtils.hasText(search)) {
            products = productRepository.searchProducts(search);
        } else if (StringUtils.hasText(category) && !category.equals("all")) {
            products = productRepository.findByCategoryAndActiveTrue(category);
        } else {
            products = productRepository.findByActiveTrue();
        }

        // Apply sorting
        products = sortProducts(products, sortBy);

        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get product by slug
    public Optional<ProductResponse> getProductBySlug(String slug) {
        log.info("Fetching product by slug: {}", slug);
        return productRepository.findBySlug(slug)
                .map(this::convertToResponse);
    }

    // Get featured products
    public List<ProductResponse> getFeaturedProducts() {
        log.info("Fetching featured products");
        return productRepository.findByFeaturedTrueAndActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get popular products
    public List<ProductResponse> getPopularProducts() {
        log.info("Fetching popular products");
        return productRepository.findByPopularTrueAndActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get new arrivals
    public List<ProductResponse> getNewArrivals() {
        log.info("Fetching new arrival products");
        return productRepository.findByNewArrivalTrueAndActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get products on sale
    public List<ProductResponse> getProductsOnSale() {
        log.info("Fetching products on sale");
        return productRepository.findByOnSaleTrueAndActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get products by category
    public List<ProductResponse> getProductsByCategory(String category) {
        log.info("Fetching products by category: {}", category);
        return productRepository.findByCategoryAndActiveTrue(category).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get products by brand
    public List<ProductResponse> getProductsByBrand(String brand) {
        log.info("Fetching products by brand: {}", brand);
        return productRepository.findByBrandAndActiveTrue(brand).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get all categories
    public List<String> getAllCategories() {
        log.info("Fetching all active categories");
        return productRepository.findAllActiveCategories();
    }

    // Get all brands
    public List<String> getAllBrands() {
        log.info("Fetching all active brands");
        return productRepository.findAllActiveBrands();
    }

    // Create product
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product: {}", request.getName());

        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setSlug(generateSlug(request.getName()));

        Product savedProduct = productRepository.save(product);
        log.info("Product created with ID: {}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    // Update product
    @Transactional
    public Optional<ProductResponse> updateProduct(String slug, ProductRequest request) {
        log.info("Updating product with slug: {}", slug);

        return productRepository.findBySlug(slug)
                .map(existingProduct -> {
                    BeanUtils.copyProperties(request, existingProduct, "id", "slug", "createdAt");

                    // Update slug if name changed
                    if (!existingProduct.getName().equals(request.getName())) {
                        existingProduct.setSlug(generateSlug(request.getName()));
                    }

                    Product savedProduct = productRepository.save(existingProduct);
                    log.info("Product updated successfully");
                    return convertToResponse(savedProduct);
                });
    }

    // Delete product (soft delete)
    @Transactional
    public boolean deleteProduct(String slug) {
        log.info("Deleting product with slug: {}", slug);

        return productRepository.findBySlug(slug)
                .map(product -> {
                    product.setActive(false);
                    productRepository.save(product);
                    log.info("Product deleted successfully");
                    return true;
                })
                .orElse(false);
    }

    // Private helper methods
    private List<Product> sortProducts(List<Product> products, String sortBy) {
        if (sortBy == null) {
            return products;
        }

        return products.stream()
                .sorted((p1, p2) -> {
                    switch (sortBy.toLowerCase()) {
                        case "price-low":
                            return p1.getPrice().compareTo(p2.getPrice());
                        case "price-high":
                            return p2.getPrice().compareTo(p1.getPrice());
                        case "rating":
                            return p2.getRating().compareTo(p1.getRating());
                        case "newest":
                            return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                        case "popular":
                            if (p1.getPopular() && !p2.getPopular())
                                return -1;
                            if (!p1.getPopular() && p2.getPopular())
                                return 1;
                            return p2.getReviewCount().compareTo(p1.getReviewCount());
                        case "name":
                            return p1.getName().compareTo(p2.getName());
                        default:
                            return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                    }
                })
                .collect(Collectors.toList());
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\s-]", "")
                .replaceAll("\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(product, response);
        return response;
    }

    // --- ADMIN METHODS ---

    // Get ALL products (Active & Inactive) for Admin
    public List<ProductResponse> getAllProductsForAdmin() {
        log.info("Fetching all products for admin");
        return productRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get product by ID (for Admin editing)
    public Optional<ProductResponse> getProductById(Long id) {
        log.info("Fetching product by ID: {}", id);
        return productRepository.findById(id)
                .map(this::convertToResponse);
    }

    // Update product by ID
    @Transactional
    public Optional<ProductResponse> updateProductById(Long id, ProductRequest request) {
        log.info("Updating product ID: {}", id);

        return productRepository.findById(id)
                .map(existingProduct -> {
                    BeanUtils.copyProperties(request, existingProduct, "id", "slug", "createdAt");

                    // Regenerate slug if name changed
                    if (!existingProduct.getName().equals(request.getName())) {
                        existingProduct.setSlug(generateSlug(request.getName()));
                    }

                    Product savedProduct = productRepository.save(existingProduct);
                    return convertToResponse(savedProduct);
                });
    }

    // Delete product by ID
    @Transactional
    public boolean deleteProductById(Long id) {
        log.info("Deleting product ID: {}", id);
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Toggle Active Status
    @Transactional
    public Optional<ProductResponse> toggleProductStatus(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setActive(!product.getActive());
                    Product saved = productRepository.save(product);
                    return convertToResponse(saved);
                });
    }
}