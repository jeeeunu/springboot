package jpabook.javaspring.features.post.repositories;

import jpabook.javaspring.features.post.domains.Post;
import jpabook.javaspring.features.user.domains.User;
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

    Page<Post> findByTitleContaining(String title, Pageable pageable);

    Page<Post> findByContentContaining(String content, Pageable pageable);

    Page<Post> findByTitleContainingAndContentContaining(String title, String content, Pageable pageable);

    Page<Post> findByAuthorId(Long userId, Pageable pageable);

    Page<Post> findByTitleContainingAndAuthorId(String title, Long userId, Pageable pageable);

    Page<Post> findByContentContainingAndAuthorId(String content, Long userId, Pageable pageable);

    Page<Post> findByTitleContainingAndContentContainingAndAuthorId(String title, String content, Long userId, Pageable pageable);
}
