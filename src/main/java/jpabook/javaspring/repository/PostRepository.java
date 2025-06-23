package jpabook.javaspring.repository;

import jpabook.javaspring.domain.post.Post;
import jpabook.javaspring.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);
}