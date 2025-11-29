package com.glowservices.glow_services_backend.repository;

import com.glowservices.glow_services_backend.model.entity.BlogPost;
import com.glowservices.glow_services_backend.model.enums.BlogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    
    // Find all blogs by status
    List<BlogPost> findByStatus(BlogStatus status);
    
    // Find all blogs by category
    List<BlogPost> findByCategory(String category);
    
    // Find all blogs by status and category
    List<BlogPost> findByStatusAndCategory(BlogStatus status, String category);
    
    // Find featured blogs
    List<BlogPost> findByFeaturedTrue();
    
    // Find featured published blogs
    List<BlogPost> findByFeaturedTrueAndStatus(BlogStatus status);
    
    // Find published blogs ordered by publish date
    @Query("SELECT b FROM BlogPost b WHERE b.status = 'PUBLISHED' ORDER BY b.publishDate DESC")
    List<BlogPost> findPublishedBlogsOrderByDate();
    
    // Find by slug (for clean URLs and unique identification)
    Optional<BlogPost> findBySlug(String slug);
    
    // Search blogs by title, content, or excerpt
    @Query("SELECT b FROM BlogPost b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<BlogPost> searchBlogs(@Param("keyword") String keyword);
    
    // Search published blogs only
    @Query("SELECT b FROM BlogPost b WHERE b.status = 'PUBLISHED' AND " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<BlogPost> searchPublishedBlogs(@Param("keyword") String keyword);
    
    // Count blogs by status
    long countByStatus(BlogStatus status);
    
    // Find top N most viewed blogs
    List<BlogPost> findTop10ByStatusOrderByViewsDesc(BlogStatus status);
    
    // Find blogs by author
    List<BlogPost> findByAuthor(String author);
    
    // Find blogs by tag (requires custom query since tags are in ElementCollection)
    @Query("SELECT DISTINCT b FROM BlogPost b JOIN b.tags t WHERE t = :tag")
    List<BlogPost> findByTag(@Param("tag") String tag);
}
