package com.glowservices.glow_services_backend.service;

import com.glowservices.glow_services_backend.model.entity.BlogPost;
import com.glowservices.glow_services_backend.model.enums.BlogStatus;
import com.glowservices.glow_services_backend.repository.BlogPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BlogPostService {

    @Autowired
    private BlogPostRepository blogPostRepository;

    // ===================== CREATE =====================
    @Transactional
    public BlogPost createBlog(BlogPost blogPost) {
        // Validate
        validateBlog(blogPost);
        
        // Generate slug if not provided
        if (blogPost.getSlug() == null || blogPost.getSlug().trim().isEmpty()) {
            String generatedSlug = generateSlug(blogPost.getTitle());
            blogPost.setSlug(ensureUniqueSlug(generatedSlug, null));
        } else {
            blogPost.setSlug(ensureUniqueSlug(blogPost.getSlug(), null));
        }
        
        // Set defaults
        if (blogPost.getViews() == null) {
            blogPost.setViews(0L);
        }
        if (blogPost.getLikes() == null) {
            blogPost.setLikes(0L);
        }
        if (blogPost.getComments() == null) {
            blogPost.setComments(0L);
        }
        
        // If published and no publish date, set it now
        if (blogPost.getStatus() == BlogStatus.PUBLISHED && blogPost.getPublishDate() == null) {
            blogPost.setPublishDate(LocalDateTime.now());
        }
        
        return blogPostRepository.save(blogPost);
    }

    // ===================== READ =====================
    public List<BlogPost> getAllBlogs() {
        return blogPostRepository.findAll();
    }

    public BlogPost getBlogById(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog post not found with id: " + id));
    }

    public BlogPost getBlogBySlug(String slug) {
        return blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Blog post not found with slug: " + slug));
    }

    public List<BlogPost> getPublishedBlogs() {
        return blogPostRepository.findByStatus(BlogStatus.PUBLISHED);
    }

    public List<BlogPost> getFeaturedBlogs() {
        return blogPostRepository.findByFeaturedTrueAndStatus(BlogStatus.PUBLISHED);
    }

    public List<BlogPost> getBlogsByCategory(String category) {
        return blogPostRepository.findByStatusAndCategory(BlogStatus.PUBLISHED, category);
    }

    public List<BlogPost> getBlogsByStatus(BlogStatus status) {
        return blogPostRepository.findByStatus(status);
    }

    public List<BlogPost> searchBlogs(String keyword) {
        return blogPostRepository.searchPublishedBlogs(keyword);
    }

    public List<BlogPost> searchAllBlogs(String keyword) {
        return blogPostRepository.searchBlogs(keyword);
    }

    public List<BlogPost> getBlogsByAuthor(String author) {
        return blogPostRepository.findByAuthor(author);
    }

    public List<BlogPost> getBlogsByTag(String tag) {
        return blogPostRepository.findByTag(tag);
    }

    public List<BlogPost> getTopViewedBlogs() {
        return blogPostRepository.findTop10ByStatusOrderByViewsDesc(BlogStatus.PUBLISHED);
    }

    // ===================== UPDATE =====================
    @Transactional
    public BlogPost updateBlog(Long id, BlogPost updatedBlog) {
        BlogPost existingBlog = getBlogById(id);
        
        // Update fields
        if (updatedBlog.getTitle() != null && !updatedBlog.getTitle().trim().isEmpty()) {
            existingBlog.setTitle(updatedBlog.getTitle());
            
            // Regenerate slug if title changed and slug is auto-generated
            if (updatedBlog.getSlug() == null || updatedBlog.getSlug().trim().isEmpty()) {
                String newSlug = generateSlug(updatedBlog.getTitle());
                existingBlog.setSlug(ensureUniqueSlug(newSlug, id));
            }
        }
        
        if (updatedBlog.getSlug() != null && !updatedBlog.getSlug().trim().isEmpty()) {
            existingBlog.setSlug(ensureUniqueSlug(updatedBlog.getSlug(), id));
        }
        
        if (updatedBlog.getExcerpt() != null) {
            existingBlog.setExcerpt(updatedBlog.getExcerpt());
        }
        
        if (updatedBlog.getContent() != null) {
            existingBlog.setContent(updatedBlog.getContent());
        }
        
        if (updatedBlog.getCategory() != null) {
            existingBlog.setCategory(updatedBlog.getCategory());
        }
        
        if (updatedBlog.getStatus() != null) {
            // If changing to PUBLISHED and no publish date, set it now
            if (updatedBlog.getStatus() == BlogStatus.PUBLISHED && existingBlog.getPublishDate() == null) {
                existingBlog.setPublishDate(LocalDateTime.now());
            }
            existingBlog.setStatus(updatedBlog.getStatus());
        }
        
        if (updatedBlog.getAuthor() != null) {
            existingBlog.setAuthor(updatedBlog.getAuthor());
        }
        
        if (updatedBlog.getFeaturedImage() != null) {
            existingBlog.setFeaturedImage(updatedBlog.getFeaturedImage());
        }
        
        if (updatedBlog.getTags() != null) {
            existingBlog.setTags(updatedBlog.getTags());
        }
        
        if (updatedBlog.getMetaTitle() != null) {
            existingBlog.setMetaTitle(updatedBlog.getMetaTitle());
        }
        
        if (updatedBlog.getMetaDescription() != null) {
            existingBlog.setMetaDescription(updatedBlog.getMetaDescription());
        }
        
        if (updatedBlog.getPublishDate() != null) {
            existingBlog.setPublishDate(updatedBlog.getPublishDate());
        }
        
        existingBlog.setFeatured(updatedBlog.isFeatured());
        
        return blogPostRepository.save(existingBlog);
    }

    // ===================== DELETE =====================
    @Transactional
    public void deleteBlog(Long id) {
        BlogPost blog = getBlogById(id);
        blogPostRepository.delete(blog);
    }

    // ===================== ANALYTICS =====================
    @Transactional
    public void incrementViews(String slug) {
        BlogPost blog = getBlogBySlug(slug);
        blog.setViews(blog.getViews() + 1);
        blogPostRepository.save(blog);
    }

    @Transactional
    public void incrementLikes(String slug) {
        BlogPost blog = getBlogBySlug(slug);
        blog.setLikes(blog.getLikes() + 1);
        blogPostRepository.save(blog);
    }

    // ===================== HELPERS =====================
    private String generateSlug(String title) {
        return title.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")  // Remove special characters
                .replaceAll("\\s+", "-")           // Replace spaces with hyphens
                .replaceAll("-+", "-")             // Replace multiple hyphens with single
                .replaceAll("^-|-$", "");          // Remove leading/trailing hyphens
    }

    private String ensureUniqueSlug(String slug, Long existingId) {
        String uniqueSlug = slug;
        int counter = 1;
        
        while (true) {
            Optional<BlogPost> existing = blogPostRepository.findBySlug(uniqueSlug);
            
            // If no blog with this slug exists, or it's the same blog we're updating
            if (existing.isEmpty() || (existingId != null && existing.get().getId().equals(existingId))) {
                return uniqueSlug;
            }
            
            // Slug exists, try with counter
            uniqueSlug = slug + "-" + counter;
            counter++;
        }
    }

    private void validateBlog(BlogPost blog) {
        if (blog.getTitle() == null || blog.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Blog title is required");
        }
        
        if (blog.getContent() == null || blog.getContent().trim().isEmpty()) {
            throw new RuntimeException("Blog content is required");
        }
        
        if (blog.getCategory() == null || blog.getCategory().trim().isEmpty()) {
            throw new RuntimeException("Blog category is required");
        }
        
        if (blog.getAuthor() == null || blog.getAuthor().trim().isEmpty()) {
            throw new RuntimeException("Blog author is required");
        }
    }

    // ===================== STATISTICS =====================
    public long getTotalBlogs() {
        return blogPostRepository.count();
    }

    public long getPublishedBlogsCount() {
        return blogPostRepository.countByStatus(BlogStatus.PUBLISHED);
    }

    public long getDraftBlogsCount() {
        return blogPostRepository.countByStatus(BlogStatus.DRAFT);
    }
}
