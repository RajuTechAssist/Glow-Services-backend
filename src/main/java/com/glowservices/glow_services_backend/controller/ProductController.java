package com.glowservices.glow_services_backend.controller;

import com.glowservices.glow_services_backend.model.dto.request.ProductRequest;
import com.glowservices.glow_services_backend.model.dto.response.ProductResponse;
import com.glowservices.glow_services_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
// @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ProductController {

    private final ProductService productService;

    // Get all products with optional filtering
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy) {

        log.info("GET /api/products - category: {}, search: {}, sortBy: {}", category, search, sortBy);

        try {
            List<ProductResponse> products = productService.getProducts(category, search, sortBy);
            log.info("Returning {} products", products.size());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error fetching products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get product by slug
    @GetMapping("/{slug}")
    public ResponseEntity<ProductResponse> getProductBySlug(@PathVariable String slug) {
        log.info("GET /api/products/{}", slug);

        return productService.getProductBySlug(slug)
                .map(product -> {
                    log.info("Product found: {}", product.getName());
                    return ResponseEntity.ok(product);
                })
                .orElseGet(() -> {
                    log.warn("Product not found with slug: {}", slug);
                    return ResponseEntity.notFound().build();
                });
    }

    // Get featured products
    @GetMapping("/featured")
    public ResponseEntity<List<ProductResponse>> getFeaturedProducts() {
        log.info("GET /api/products/featured");

        try {
            List<ProductResponse> products = productService.getFeaturedProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error fetching featured products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get popular products
    @GetMapping("/popular")
    public ResponseEntity<List<ProductResponse>> getPopularProducts() {
        log.info("GET /api/products/popular");

        try {
            List<ProductResponse> products = productService.getPopularProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error fetching popular products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get new arrivals
    @GetMapping("/new-arrivals")
    public ResponseEntity<List<ProductResponse>> getNewArrivals() {
        log.info("GET /api/products/new-arrivals");

        try {
            List<ProductResponse> products = productService.getNewArrivals();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error fetching new arrivals", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get products on sale
    @GetMapping("/on-sale")
    public ResponseEntity<List<ProductResponse>> getProductsOnSale() {
        log.info("GET /api/products/on-sale");

        try {
            List<ProductResponse> products = productService.getProductsOnSale();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error fetching products on sale", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get products by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        log.info("GET /api/products/category/{}", category);

        try {
            List<ProductResponse> products = productService.getProductsByCategory(category);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error fetching products by category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get products by brand
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<ProductResponse>> getProductsByBrand(@PathVariable String brand) {
        log.info("GET /api/products/brand/{}", brand);

        try {
            List<ProductResponse> products = productService.getProductsByBrand(brand);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error fetching products by brand", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all categories
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        log.info("GET /api/products/categories");

        try {
            List<String> categories = productService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("Error fetching categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all brands
    @GetMapping("/brands")
    public ResponseEntity<List<String>> getAllBrands() {
        log.info("GET /api/products/brands");

        try {
            List<String> brands = productService.getAllBrands();
            return ResponseEntity.ok(brands);
        } catch (Exception e) {
            log.error("Error fetching brands", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create product (Admin only)
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        log.info("POST /api/products - Creating product: {}", request.getName());

        try {
            ProductResponse product = productService.createProduct(request);
            log.info("Product created successfully with ID: {}", product.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (Exception e) {
            log.error("Error creating product", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update product (Admin only)
    @PutMapping("/{slug}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String slug,
            @RequestBody ProductRequest request) {

        log.info("PUT /api/products/{} - Updating product", slug);

        try {
            return productService.updateProduct(slug, request)
                    .map(product -> {
                        log.info("Product updated successfully");
                        return ResponseEntity.ok(product);
                    })
                    .orElseGet(() -> {
                        log.warn("Product not found for update: {}", slug);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("Error updating product", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete product (Admin only)
    @DeleteMapping("/{slug}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable String slug) {
        log.info("DELETE /api/products/{}", slug);

        try {
            boolean deleted = productService.deleteProduct(slug);
            Map<String, Object> response = new HashMap<>();

            if (deleted) {
                response.put("message", "Product deleted successfully");
                response.put("success", true);
                log.info("Product deleted successfully: {}", slug);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Product not found");
                response.put("success", false);
                log.warn("Product not found for deletion: {}", slug);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting product", e);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Internal server error");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}