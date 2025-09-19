package com.glowservices.glow_services_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.glowservices.glow_services_backend.model.entity.BlogPost;
import com.glowservices.glow_services_backend.model.enums.BlogStatus;

@Repository
public interface BlogRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByStatus(BlogStatus status);
    List<BlogPost> findByCategory(String category);
    List<BlogPost> findByFeaturedTrue();

    @Query("SELECT b FROM BlogPost b WHERE b.title LIKE %?1% OR b.excerpt LIKE %?1%")
    List<BlogPost> findByTitleOrExcerptContaining(String searchTerm);

    @Query("SELECT b FROM BlogPost b WHERE b.status = 'PUBLISHED' ORDER BY b.publishDate DESC")
    List<BlogPost> findPublishedBlogsOrderByDate();

    @Query("SELECT SUM(b.views), SUM(b.likes), SUM(b.comments) FROM BlogPost b")
    Object[] getBlogAnalytics();
}
