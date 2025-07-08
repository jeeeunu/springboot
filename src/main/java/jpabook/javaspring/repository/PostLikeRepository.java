package jpabook.javaspring.repository;

import jpabook.javaspring.domain.post.Post;
import jpabook.javaspring.domain.post.PostLike;
import jpabook.javaspring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    Optional<PostLike> findByPostAndUser(Post post, User user);
    
    boolean existsByPostAndUser(Post post, User user);
    
    void deleteByPostAndUser(Post post, User user);
    
    long countByPost(Post post);
}