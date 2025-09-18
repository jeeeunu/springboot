package jpabook.javaspring.service;

import jpabook.javaspring.domain.post.Post;
import jpabook.javaspring.domain.post.PostLike;
import jpabook.javaspring.domain.user.User;
import jpabook.javaspring.dto.post.PostCreateDto;
import jpabook.javaspring.dto.post.PostDto;
import jpabook.javaspring.dto.post.PostUpdateDto;
import jpabook.javaspring.repository.PostLikeRepository;
import jpabook.javaspring.repository.PostRepository;
import jpabook.javaspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    public Page<PostDto> findAll(Pageable pageable, String title, String content, Long userId) {
        // 필터와 함께 동적 쿼리 사용
        return postRepository.findAllWithFilters(title, content, userId, pageable)
                .map(this::convertToDto);
    }

    public Page<PostDto> findAll(Pageable pageable, String title, String content, Long userId, Long authUserId) {

        System.out.println("authUserId " + authUserId);
        if (authUserId == null) {
            return findAll(pageable, title, content, userId);
        }

        User currentUser = userRepository.findById(authUserId)
                .orElse(null);

        System.out.println("currentUser " + currentUser);

        if (currentUser == null) {
            return findAll(pageable, title, content, userId);
        }

        return postRepository.findAllWithFilters(title, content, userId, pageable)
                .map(post -> convertToDto(post, currentUser));
    }

    public Page<PostDto> findByAuthor(Long userId, Pageable pageable) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        return postRepository.findByAuthor(author, pageable)
                .map(this::convertToDto);
    }

    public Page<PostDto> findByAuthor(Long userId, Pageable pageable, Long currentUserId) {
        if (currentUserId == null) {
            return findByAuthor(userId, pageable);
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        User currentUser = userRepository.findById(currentUserId)
                .orElse(null);

        if (currentUser == null) {
            return findByAuthor(userId, pageable);
        }

        return postRepository.findByAuthor(author, pageable)
                .map(post -> convertToDto(post, currentUser));
    }

    public PostDto findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + id));
        long likeCount = postLikeRepository.countByPost(post);
        return PostDto.fromEntity(post, likeCount);
    }

    public PostDto findById(Long id, Long currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + id));
        long likeCount = postLikeRepository.countByPost(post);

        if (currentUserId == null) {
            return PostDto.fromEntity(post, likeCount);
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElse(null);

        if (currentUser == null) {
            return PostDto.fromEntity(post, likeCount);
        }

        boolean liked = postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUser.getId());
        return PostDto.fromEntity(post, likeCount, liked);
    }

    private PostDto convertToDto(Post post) {
        long likeCount = postLikeRepository.countByPost(post);
        return PostDto.fromEntity(post, likeCount);
    }

    private PostDto convertToDto(Post post, User currentUser) {
        long likeCount = postLikeRepository.countByPost(post);
        System.out.println("likeCount " + likeCount);
        boolean liked = postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUser.getId());
        return PostDto.fromEntity(post, likeCount, liked);
    }

    @Transactional
    public PostDto create(PostCreateDto createDto, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        Post post = createDto.toEntity(author);
        Post savedPost = postRepository.save(post);

        return convertToDto(savedPost, author);
    }

    @Transactional
    public PostDto update(Long id, PostUpdateDto updateDto, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        if (!post.getAuthor().getUsername().equals(userId)) {
            throw new AccessDeniedException("이 게시물을 수정할 권한이 없습니다");
        }

        post.update(updateDto.getTitle(), updateDto.getContent());
        return convertToDto(post, user);
    }

    @Transactional
    public void delete(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + id));

        if (!post.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("이 게시물을 삭제할 권한이 없습니다");
        }

        postRepository.delete(post);
    }

    @Transactional
    public void likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        if (!postLikeRepository.existsByPostIdAndUserId(post.getId(), user.getId())) {
            PostLike postLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();

            postLikeRepository.save(postLike);
        }
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        postLikeRepository.deleteByPostAndUser(post, user);
    }

    public boolean hasUserLikedPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        return postLikeRepository.existsByPostIdAndUserId(post.getId(), user.getId());
    }

    public long getPostLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + postId));

        return postLikeRepository.countByPost(post);
    }
}
