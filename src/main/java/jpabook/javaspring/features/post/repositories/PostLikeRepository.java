package jpabook.javaspring.features.post.repositories;

import jpabook.javaspring.features.post.domains.Post;
import jpabook.javaspring.features.post.domains.PostLike;
import jpabook.javaspring.features.user.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    Optional<PostLike> findByPostAndUser(Post post, User user);

    boolean existsByPostIdAndUserId(Long postId, Long userId);
    
    void deleteByPostAndUser(Post post, User user);
    
    long countByPost(Post post);
}