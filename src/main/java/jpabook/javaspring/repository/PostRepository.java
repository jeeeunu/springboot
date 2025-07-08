package jpabook.javaspring.repository;

import jpabook.javaspring.domain.post.Post;
import jpabook.javaspring.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    Page<Post> findAll(Pageable pageable);
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findByAuthor(User author, Pageable pageable);
    Page<Post> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);

    // Filter by title
    Page<Post> findByTitleContaining(String title, Pageable pageable);

    // Filter by content
    Page<Post> findByContentContaining(String content, Pageable pageable);

    // Filter by title and content
    Page<Post> findByTitleContainingAndContentContaining(String title, String content, Pageable pageable);

    // Filter by userId
    Page<Post> findByAuthorId(Long userId, Pageable pageable);

    // Filter by both title and userId
    Page<Post> findByTitleContainingAndAuthorId(String title, Long userId, Pageable pageable);

    // Filter by both content and userId
    Page<Post> findByContentContainingAndAuthorId(String content, Long userId, Pageable pageable);

    // Filter by title, content, and userId
    Page<Post> findByTitleContainingAndContentContainingAndAuthorId(String title, String content, Long userId, Pageable pageable);
}
