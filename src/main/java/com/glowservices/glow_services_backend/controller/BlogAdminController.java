package com.glowservices.glow_services_backend.controller;

import com.glowservices.glow_services_backend.model.entity.BlogPost;
import com.glowservices.glow_services_backend.model.enums.BlogStatus;
import com.glowservices.glow_services_backend.service.BlogPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/blogs")
@CrossOrigin(origins = {"http://localhost:5173", "https://glow-service.studio", "https://www.glow-service.studio"}, allowCredentials = "true")
public class BlogAdminController {

    @Autowired
    private BlogPostService blogPostService;

    // ===================== CREATE =====================
    @PostMapping
    public ResponseEntity<?> createBlog(@RequestBody BlogPost blogPost) {
        try {
            BlogPost createdBlog = blogPostService.createBlog(blogPost);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBlog);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create blog post: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ===================== READ =====================
    @GetMapping
    public ResponseEntity<List<BlogPost>> getAllBlogs(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String search
    ) {
        try {
            List<BlogPost> blogs;

            // Apply filters
            if (search != null && !search.trim().isEmpty()) {
                blogs = blogPostService.searchAllBlogs(search);
            } else if (status != null && !status.trim().isEmpty()) {
                BlogStatus blogStatus = BlogStatus.valueOf(status.toUpperCase());
                blogs = blogPostService.getBlogsByStatus(blogStatus);
            } else if (category != null && !category.trim().isEmpty()) {
                blogs = blogPostService.getBlogsByCategory(category);
            } else if (author != null && !author.trim().isEmpty()) {
                blogs = blogPostService.getBlogsByAuthor(author);
            } else {
                blogs = blogPostService.getAllBlogs();
            }

            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBlogById(@PathVariable Long id) {
        try {
            BlogPost blog = blogPostService.getBlogById(id);
            return ResponseEntity.ok(blog);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getBlogBySlug(@PathVariable String slug) {
        try {
            BlogPost blog = blogPostService.getBlogBySlug(slug);
            return ResponseEntity.ok(blog);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ===================== UPDATE =====================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(@PathVariable Long id, @RequestBody BlogPost blogPost) {
        try {
            BlogPost updatedBlog = blogPostService.updateBlog(id, blogPost);
            return ResponseEntity.ok(updatedBlog);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update blog post: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ===================== DELETE =====================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable Long id) {
        try {
            blogPostService.deleteBlog(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Blog post deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete blog post: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ===================== STATISTICS =====================
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getBlogStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalBlogs", blogPostService.getTotalBlogs());
            stats.put("publishedBlogs", blogPostService.getPublishedBlogsCount());
            stats.put("draftBlogs", blogPostService.getDraftBlogsCount());
            
            List<BlogPost> topViewed = blogPostService.getTopViewedBlogs();
            stats.put("topViewedBlogs", topViewed);
            
            // Calculate total views, likes, comments
            List<BlogPost> allBlogs = blogPostService.getAllBlogs();
            long totalViews = allBlogs.stream().mapToLong(BlogPost::getViews).sum();
            long totalLikes = allBlogs.stream().mapToLong(BlogPost::getLikes).sum();
            long totalComments = allBlogs.stream().mapToLong(BlogPost::getComments).sum();
            
            stats.put("totalViews", totalViews);
            stats.put("totalLikes", totalLikes);
            stats.put("totalComments", totalComments);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===================== FEATURED MANAGEMENT =====================
    @PatchMapping("/{id}/featured")
    public ResponseEntity<?> toggleFeatured(@PathVariable Long id) {
        try {
            BlogPost blog = blogPostService.getBlogById(id);
            blog.setFeatured(!blog.isFeatured());
            BlogPost updatedBlog = blogPostService.updateBlog(id, blog);
            return ResponseEntity.ok(updatedBlog);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ===================== STATUS MANAGEMENT =====================
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        try {
            String status = statusUpdate.get("status");
            if (status == null || status.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Status is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            BlogPost blog = blogPostService.getBlogById(id);
            blog.setStatus(BlogStatus.valueOf(status.toUpperCase()));
            BlogPost updatedBlog = blogPostService.updateBlog(id, blog);
            return ResponseEntity.ok(updatedBlog);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid status value");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
