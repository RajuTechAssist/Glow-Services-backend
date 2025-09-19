package com.glowservices.glow_services_backend.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.glowservices.glow_services_backend.model.entity.BlogPost;
import com.glowservices.glow_services_backend.model.enums.BlogStatus;
import com.glowservices.glow_services_backend.repository.BlogRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    public List<BlogPost> getAllBlogs() {
        try {
            return blogRepository.findAll();
        } catch (DataAccessException e) {
            System.err.println("Database error fetching blogs: " + e.getMessage());
            throw new RuntimeException("Failed to fetch blogs");
        }
    }

    public BlogPost getBlogById(Long id) {
        try {
            return blogRepository.findById(id).orElse(null);
        } catch (DataAccessException e) {
            System.err.println("Database error fetching blog: " + e.getMessage());
            return null;
        }
    }

    public BlogPost createBlog(BlogPost blog) {
        try {
            blog.setCreatedAt(LocalDateTime.now());
            blog.setUpdatedAt(LocalDateTime.now());

            if (blog.getPublishDate() == null && blog.getStatus() == BlogStatus.PUBLISHED) {
                blog.setPublishDate(LocalDateTime.now());
            }

            return blogRepository.save(blog);
        } catch (DataAccessException e) {
            System.err.println("Database error creating blog: " + e.getMessage());
            throw new RuntimeException("Failed to create blog");
        }
    }

    public BlogPost updateBlog(Long id, BlogPost blogDetails) {
        try {
            Optional<BlogPost> blogOpt = blogRepository.findById(id);
            if (blogOpt.isPresent()) {
                BlogPost blog = blogOpt.get();

                blog.setTitle(blogDetails.getTitle());
                blog.setExcerpt(blogDetails.getExcerpt());
                blog.setContent(blogDetails.getContent());
                blog.setCategory(blogDetails.getCategory());
                blog.setStatus(blogDetails.getStatus());
                blog.setAuthor(blogDetails.getAuthor());
                blog.setFeaturedImage(blogDetails.getFeaturedImage());
                blog.setTags(blogDetails.getTags());
                blog.setMetaTitle(blogDetails.getMetaTitle());
                blog.setMetaDescription(blogDetails.getMetaDescription());
                blog.setPublishDate(blogDetails.getPublishDate());
                blog.setFeatured(blogDetails.isFeatured());
                blog.setUpdatedAt(LocalDateTime.now());

                return blogRepository.save(blog);
            }
            throw new RuntimeException("Blog not found");
        } catch (DataAccessException e) {
            System.err.println("Database error updating blog: " + e.getMessage());
            throw new RuntimeException("Failed to update blog");
        }
    }

    public boolean deleteBlog(Long id) {
        try {
            if (blogRepository.existsById(id)) {
                blogRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (DataAccessException e) {
            System.err.println("Database error deleting blog: " + e.getMessage());
            throw new RuntimeException("Failed to delete blog");
        }
    }

    public List<BlogPost> getPublishedBlogs() {
        try {
            return blogRepository.findByStatus(BlogStatus.PUBLISHED);
        } catch (DataAccessException e) {
            System.err.println("Database error fetching published blogs: " + e.getMessage());
            throw new RuntimeException("Failed to fetch published blogs");
        }
    }

    public List<BlogPost> getFeaturedBlogs() {
        try {
            return blogRepository.findByFeaturedTrue();
        } catch (DataAccessException e) {
            System.err.println("Database error fetching featured blogs: " + e.getMessage());
            throw new RuntimeException("Failed to fetch featured blogs");
        }
    }

    public Map<String, Object> getBlogAnalytics() {
        try {
            Map<String, Object> analytics = new HashMap<>();

            Long totalBlogs = blogRepository.count();
            Long publishedBlogs = (long) blogRepository.findByStatus(BlogStatus.PUBLISHED).size();

            Object[] stats = blogRepository.getBlogAnalytics();
            Long totalViews = stats != null ? ((Number) stats[0]).longValue() : 0L;
            Long totalLikes = stats != null ? ((Number) stats[1]).longValue() : 0L;
            Long totalComments = stats != null ? ((Number) stats[2]).longValue() : 0L;

            analytics.put("totalBlogs", totalBlogs);
            analytics.put("publishedBlogs", publishedBlogs);
            analytics.put("totalViews", totalViews);
            analytics.put("totalLikes", totalLikes);
            analytics.put("totalComments", totalComments);

            return analytics;
        } catch (DataAccessException e) {
            System.err.println("Database error fetching blog analytics: " + e.getMessage());
            throw new RuntimeException("Failed to fetch blog analytics");
        }
    }
}
