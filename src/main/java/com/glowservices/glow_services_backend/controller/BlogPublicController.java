package com.glowservices.glow_services_backend.controller;

import com.glowservices.glow_services_backend.model.entity.BlogPost;
import com.glowservices.glow_services_backend.service.BlogPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blogs")
// @CrossOrigin(origins = { "http://localhost:5173", "https://glow-service.studio",
//         "https://www.glow-service.studio" }, allowCredentials = "true")
public class BlogPublicController {

    @Autowired
    private BlogPostService blogPostService;

    // ===================== PUBLIC READ ENDPOINTS =====================

    @GetMapping("/debug")
    public ResponseEntity<?> debugBlogs() {
        try {
            List<BlogPost> allBlogs = blogPostService.getAllBlogs();
            Map<String, Object> debug = new HashMap<>();
            debug.put("total", allBlogs.size());
            debug.put("blogs", allBlogs.stream().map(b -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", b.getId());
                m.put("title", b.getTitle());
                m.put("status", b.getStatus()); // Check actual status value
                return m;
            }).toList());
            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }

    /**
     * Get all published blogs
     */
    @GetMapping
    public ResponseEntity<List<BlogPost>> getAllPublishedBlogs() {
        try {
            List blogs = blogPostService.getPublishedBlogs();
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get featured published blogs
     */
    @GetMapping("/featured")
    public ResponseEntity<List<BlogPost>> getFeaturedBlogs() {
        try {
            List<BlogPost> blogs = blogPostService.getFeaturedBlogs();
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get single blog by slug (SEO-friendly URL)
     * Example: /api/blogs/slug/summer-skincare-tips
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getBlogBySlug(@PathVariable String slug) {
        try {
            BlogPost blog = blogPostService.getBlogBySlug(slug);
            return ResponseEntity.ok(blog);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Blog post not found");
            error.put("slug", slug);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Get blogs by category
     * Example: /api/blogs/category/Skincare
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<BlogPost>> getBlogsByCategory(@PathVariable String category) {
        try {
            List<BlogPost> blogs = blogPostService.getBlogsByCategory(category);
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search published blogs
     * Example: /api/blogs/search?q=skincare
     */
    @GetMapping("/search")
    public ResponseEntity<List<BlogPost>> searchBlogs(@RequestParam("q") String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResponseEntity.ok(blogPostService.getPublishedBlogs());
            }
            List<BlogPost> blogs = blogPostService.searchBlogs(keyword);
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get blogs by tag
     * Example: /api/blogs/tag/summer
     */
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<BlogPost>> getBlogsByTag(@PathVariable String tag) {
        try {
            List<BlogPost> blogs = blogPostService.getBlogsByTag(tag);
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get top viewed blogs
     */
    @GetMapping("/popular")
    public ResponseEntity<List<BlogPost>> getPopularBlogs() {
        try {
            List<BlogPost> blogs = blogPostService.getTopViewedBlogs();
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===================== ENGAGEMENT ENDPOINTS =====================

    /**
     * Increment view count for a blog post
     * Called when user opens a blog post
     */
    @PostMapping("/slug/{slug}/view")
    public ResponseEntity<?> incrementViews(@PathVariable String slug) {
        try {
            blogPostService.incrementViews(slug);
            Map<String, String> response = new HashMap<>();
            response.put("message", "View count incremented");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Blog post not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Increment like count for a blog post
     */
    @PostMapping("/slug/{slug}/like")
    public ResponseEntity<?> incrementLikes(@PathVariable String slug) {
        try {
            blogPostService.incrementLikes(slug);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Like count incremented");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Blog post not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ===================== METADATA ENDPOINTS =====================

    /**
     * Get all available categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        try {
            List<BlogPost> allPublishedBlogs = blogPostService.getPublishedBlogs();
            List<String> categories = allPublishedBlogs.stream()
                    .map(BlogPost::getCategory)
                    .distinct()
                    .sorted()
                    .toList();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all available tags
     */
    @GetMapping("/tags")
    public ResponseEntity<List<String>> getTags() {
        try {
            List<BlogPost> allPublishedBlogs = blogPostService.getPublishedBlogs();
            List<String> tags = allPublishedBlogs.stream()
                    .flatMap(blog -> blog.getTags().stream())
                    .distinct()
                    .sorted()
                    .toList();
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
