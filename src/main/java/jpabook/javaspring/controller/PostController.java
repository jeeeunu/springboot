package jpabook.javaspring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jpabook.javaspring.dto.common.ApiResponse;
import jpabook.javaspring.dto.post.PostCreateDto;
import jpabook.javaspring.dto.post.PostDto;
import jpabook.javaspring.dto.post.PostUpdateDto;
import jpabook.javaspring.service.PostService;
import jpabook.javaspring.util.PaginationUtil;
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
@Tag(name = "Posts", description = "게시글 관리 API")
public class PostController {

    private final PostService postService;

    @GetMapping
    @Operation(
            summary = "게시글 목록 조회"
    )
    public ResponseEntity<ApiResponse<Page<PostDto>>> getAllPosts(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        int zeroBasedPage = PaginationUtil.toZeroBasedPage(pageable.getPageNumber());

        Pageable adjustedPageable = org.springframework.data.domain.PageRequest.of(
            zeroBasedPage, 
            pageable.getPageSize(), 
            pageable.getSort()
        );

        Page<PostDto> posts = postService.findAll(adjustedPageable);
        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회가 완료되었습니다.", posts));
    }

    @GetMapping("/user/{username}")
    @Operation(
            summary = "사용자 게시글 조회"
    )
    public ResponseEntity<ApiResponse<Page<PostDto>>> getPostsByUser(
            @PathVariable String username,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        int zeroBasedPage = PaginationUtil.toZeroBasedPage(pageable.getPageNumber());

        Pageable adjustedPageable = org.springframework.data.domain.PageRequest.of(
            zeroBasedPage, 
            pageable.getPageSize(), 
            pageable.getSort()
        );

        Page<PostDto> posts = postService.findByAuthor(username, adjustedPageable);
        return ResponseEntity.ok(ApiResponse.success("사용자 게시글 조회가 완료되었습니다.", posts));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "게시글 단일 조회"
    )
    public ResponseEntity<ApiResponse<PostDto>> getPostById(@PathVariable Long id) {
        PostDto post = postService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("게시글 조회가 완료되었습니다.", post));
    }

    @PostMapping
    @Operation(
            summary = "게시글 작성",
            security = { @SecurityRequirement(name = "bearer-key") }
    )
    public ResponseEntity<ApiResponse<PostDto>> createPost(
            @Valid @RequestBody PostCreateDto createDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostDto createdPost = postService.create(createDto, userDetails.getUsername());
        return new ResponseEntity<>(ApiResponse.success("게시글 작성이 완료되었습니다.", createdPost), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "게시글 수정",
            security = { @SecurityRequirement(name = "bearer-key") }
    )
    public ResponseEntity<ApiResponse<PostDto>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostDto updatedPost = postService.update(id, updateDto, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("게시글 수정이 완료되었습니다.", updatedPost));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "게시글 삭제",
            security = { @SecurityRequirement(name = "bearer-key") }
    )
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.delete(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("게시글 삭제가 완료되었습니다."));
    }
}
