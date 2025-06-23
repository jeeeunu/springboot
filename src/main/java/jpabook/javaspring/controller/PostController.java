package jpabook.javaspring.controller;

import jakarta.validation.Valid;
import jpabook.javaspring.dto.common.ApiResponse;
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
    public ResponseEntity<ApiResponse<Page<PostDto>>> getAllPosts(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<PostDto> posts = postService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회가 완료되었습니다.", posts));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PostDto>>> searchPosts(
            @RequestParam String query,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<PostDto> posts = postService.search(query, pageable);
        return ResponseEntity.ok(ApiResponse.success("게시글 검색이 완료되었습니다.", posts));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<Page<PostDto>>> getPostsByUser(
            @PathVariable String username,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<PostDto> posts = postService.findByAuthor(username, pageable);
        return ResponseEntity.ok(ApiResponse.success("사용자 게시글 조회가 완료되었습니다.", posts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDto>> getPostById(@PathVariable Long id) {
        PostDto post = postService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("게시글 조회가 완료되었습니다.", post));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostDto>> createPost(
            @Valid @RequestBody PostCreateDto createDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostDto createdPost = postService.create(createDto, userDetails.getUsername());
        return new ResponseEntity<>(ApiResponse.success("게시글 작성이 완료되었습니다.", createdPost), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDto>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostDto updatedPost = postService.update(id, updateDto, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("게시글 수정이 완료되었습니다.", updatedPost));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.delete(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("게시글 삭제가 완료되었습니다."));
    }
}
