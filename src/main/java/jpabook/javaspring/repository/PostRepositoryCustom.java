package jpabook.javaspring.repository;

import jpabook.javaspring.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository interface for Post entity to handle dynamic queries
 */
public interface PostRepositoryCustom {
    
    /**
     * Find posts with dynamic filtering
     * 
     * @param title Optional title filter (contains)
     * @param content Optional content filter (contains)
     * @param userId Optional user ID filter
     * @param pageable Pagination information
     * @return Page of Post entities matching the criteria
     */
    Page<Post> findAllWithFilters(String title, String content, Long userId, Pageable pageable);
}