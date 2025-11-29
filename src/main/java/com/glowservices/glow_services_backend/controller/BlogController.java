// package com.glowservices.glow_services_backend.controller;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.springframework.http.HttpStatus;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.glowservices.glow_services_backend.model.entity.BlogPost;
// import com.glowservices.glow_services_backend.service.BlogService;

// import io.swagger.v3.oas.annotations.parameters.RequestBody;

// @RestController
// @RequestMapping("/api/admin/blogs")
// @CrossOrigin(origins = {"http://localhost:5173", "https://glow-services-frontend.vercel.app"})
// public class BlogController {

//     @Autowired
//     private BlogService blogService;

//     @GetMapping
//     public ResponseEntity<?> getAllBlogs() {
//         try {
//             List<BlogPost> blogs = blogService.getAllBlogs();
//             System.out.println("📚 Fetched " + blogs.size() + " blogs");
//             return ResponseEntity.ok(blogs);
//         } catch (Exception e) {
//             System.err.println("❌ Error fetching blogs: " + e.getMessage());
//             Map<String, String> error = new HashMap<>();
//             error.put("message", "Failed to fetch blogs");
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//         }
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<?> getBlogById(@PathVariable Long id) {
//         try {
//             BlogPost blog = blogService.getBlogById(id);
//             if (blog != null) {
//                 return ResponseEntity.ok(blog);
//             } else {
//                 Map<String, String> error = new HashMap<>();
//                 error.put("message", "Blog not found");
//                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//             }
//         } catch (Exception e) {
//             System.err.println("❌ Error fetching blog: " + e.getMessage());
//             Map<String, String> error = new HashMap<>();
//             error.put("message", "Failed to fetch blog");
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//         }
//     }

//     @PostMapping
//     public ResponseEntity<?> createBlog(@RequestBody BlogPost blog) {
//         try {
//             System.out.println("📝 Creating new blog: " + blog.getTitle());
//             BlogPost createdBlog = blogService.createBlog(blog);
//             System.out.println("✅ Blog created successfully with ID: " + createdBlog.getId());
//             return ResponseEntity.ok(createdBlog);
//         } catch (Exception e) {
//             System.err.println("❌ Error creating blog: " + e.getMessage());
//             Map<String, String> error = new HashMap<>();
//             error.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
//         }
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<?> updateBlog(@PathVariable Long id, @RequestBody BlogPost blog) {
//         try {
//             System.out.println("📝 Updating blog with ID: " + id);
//             BlogPost updatedBlog = blogService.updateBlog(id, blog);
//             System.out.println("✅ Blog updated successfully");
//             return ResponseEntity.ok(updatedBlog);
//         } catch (Exception e) {
//             System.err.println("❌ Error updating blog: " + e.getMessage());
//             Map<String, String> error = new HashMap<>();
//             error.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
//         }
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<?> deleteBlog(@PathVariable Long id) {
//         try {
//             System.out.println("🗑️ Deleting blog with ID: " + id);
//             boolean deleted = blogService.deleteBlog(id);
//             if (deleted) {
//                 Map<String, String> response = new HashMap<>();
//                 response.put("message", "Blog deleted successfully");
//                 System.out.println("✅ Blog deleted successfully");
//                 return ResponseEntity.ok(response);
//             } else {
//                 Map<String, String> error = new HashMap<>();
//                 error.put("message", "Blog not found");
//                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//             }
//         } catch (Exception e) {
//             System.err.println("❌ Error deleting blog: " + e.getMessage());
//             Map<String, String> error = new HashMap<>();
//             error.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//         }
//     }

//     @GetMapping("/analytics")
//     public ResponseEntity<?> getBlogAnalytics() {
//         try {
//             Map<String, Object> analytics = blogService.getBlogAnalytics();
//             System.out.println("📊 Fetched blog analytics");
//             return ResponseEntity.ok(analytics);
//         } catch (Exception e) {
//             System.err.println("❌ Error fetching blog analytics: " + e.getMessage());
//             Map<String, String> error = new HashMap<>();
//             error.put("message", "Failed to fetch analytics");
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//         }
//     }

//     @GetMapping("/published")
//     public ResponseEntity<?> getPublishedBlogs() {
//         try {
//             List<BlogPost> blogs = blogService.getPublishedBlogs();
//             return ResponseEntity.ok(blogs);
//         } catch (Exception e) {
//             System.err.println("❌ Error fetching published blogs: " + e.getMessage());
//             Map<String, String> error = new HashMap<>();
//             error.put("message", "Failed to fetch published blogs");
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//         }
//     }

//     @GetMapping("/featured")
//     public ResponseEntity<?> getFeaturedBlogs() {
//         try {
//             List<BlogPost> blogs = blogService.getFeaturedBlogs();
//             return ResponseEntity.ok(blogs);
//         } catch (Exception e) {
//             System.err.println("❌ Error fetching featured blogs: " + e.getMessage());
//             Map<String, String> error = new HashMap<>();
//             error.put("message", "Failed to fetch featured blogs");
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//         }
//     }
// }