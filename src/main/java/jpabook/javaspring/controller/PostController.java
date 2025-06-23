package jpabook.javaspring.controller;

import jakarta.validation.Valid;
import jpabook.javaspring.dto.post.PostCreateDto;
import jpabook.javaspring.dto.post.PostDto;
import jpabook.javaspring.dto.post.PostUpdateDto;
import jpabook.javaspring.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostDto>> getAllPosts(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<PostDto> posts = postService.findAll(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostDto>> searchPosts(
            @RequestParam String query,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<PostDto> posts = postService.search(query, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Page<PostDto>> getPostsByUser(
            @PathVariable String username,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<PostDto> posts = postService.findByAuthor(username, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        PostDto post = postService.findById(id);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @Valid @RequestBody PostCreateDto createDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostDto createdPost = postService.create(createDto, userDetails.getUsername());
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostDto updatedPost = postService.update(id, updateDto, userDetails.getUsername());
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}