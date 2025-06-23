package jpabook.javaspring.service;

import jpabook.javaspring.domain.post.Post;
import jpabook.javaspring.domain.user.User;
import jpabook.javaspring.dto.post.PostCreateDto;
import jpabook.javaspring.dto.post.PostDto;
import jpabook.javaspring.dto.post.PostUpdateDto;
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

    public Page<PostDto> findAll(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(PostDto::fromEntity);
    }

    public Page<PostDto> findByAuthor(String username, Pageable pageable) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        return postRepository.findByAuthorOrderByCreatedAtDesc(author, pageable)
                .map(PostDto::fromEntity);
    }

    public PostDto findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + id));
        return PostDto.fromEntity(post);
    }

    @Transactional
    public PostDto create(PostCreateDto createDto, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        Post post = createDto.toEntity(author);
        Post savedPost = postRepository.save(post);
        return PostDto.fromEntity(savedPost);
    }

    @Transactional
    public PostDto update(Long id, PostUpdateDto updateDto, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + id));

        if (!post.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("이 게시물을 수정할 권한이 없습니다");
        }

        post.update(updateDto.getTitle(), updateDto.getContent());
        return PostDto.fromEntity(post);
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
}
