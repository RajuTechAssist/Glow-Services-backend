package com.glowservices.glow_services_backend.controller;

import com.glowservices.glow_services_backend.model.dto.request.ProductRequest;
import com.glowservices.glow_services_backend.model.dto.response.ProductResponse;
import com.glowservices.glow_services_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
// @CrossOrigin(origins = {
//     "http://localhost:5173", 
//     "https://glow-service.studio", 
//     "https://www.glow-service.studio"
// }, allowCredentials = "true")
public class ProductAdminController {

    @Autowired
    private ProductService productService;

    // Get all products (including inactive)
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProductsForAdmin());
    }

    // Get single product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new product
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(request));
    }

    // Update product
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return productService.updateProductById(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.deleteProductById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Toggle status (Active/Inactive)
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ProductResponse> toggleStatus(@PathVariable Long id) {
        return productService.toggleProductStatus(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}