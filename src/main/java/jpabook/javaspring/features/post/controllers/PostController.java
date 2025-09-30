package jpabook.javaspring.features.post.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jpabook.javaspring.features.user.domains.CustomUserDetails;
import jpabook.javaspring.common.dto.ApiResponse;
import jpabook.javaspring.common.dto.PageResponse;
import jpabook.javaspring.features.post.dtos.PostCreateDto;
import jpabook.javaspring.features.post.dtos.PostDto;
import jpabook.javaspring.features.post.dtos.PostUpdateDto;
import jpabook.javaspring.features.post.services.PostService;
import jpabook.javaspring.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "게시글 관리 API")
public class PostController {

    private final PostService postService;

    @GetMapping
    @Operation(
            summary = "게시글 목록 조회",
            security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<ApiResponse<PageResponse<PostDto>>> getAllPosts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Long userId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        int zeroBasedPage = PaginationUtil.toZeroBasedPage(pageable.getPageNumber());

        Pageable adjustedPageable = org.springframework.data.domain.PageRequest.of(
            zeroBasedPage,
            pageable.getPageSize(),
            pageable.getSort()
        );

        Page<PostDto> posts = postService.findAll(adjustedPageable, title, content, userId, userDetails.getId());
        PageResponse<PostDto> pageResponse = PageResponse.from(posts);
        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회가 완료되었습니다.", pageResponse));
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "사용자 게시글 조회"
    )
    public ResponseEntity<ApiResponse<PageResponse<PostDto>>> getPostsByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        int zeroBasedPage = PaginationUtil.toZeroBasedPage(pageable.getPageNumber());

        Pageable adjustedPageable = org.springframework.data.domain.PageRequest.of(
            zeroBasedPage,
            pageable.getPageSize(),
            pageable.getSort()
        );

        Long currentUserId = userDetails != null ? userDetails.getId() : null;
        Page<PostDto> posts = postService.findByAuthor(userId, adjustedPageable, currentUserId);
        PageResponse<PostDto> pageResponse = PageResponse.from(posts);
        return ResponseEntity.ok(ApiResponse.success("사용자 게시글 조회가 완료되었습니다.", pageResponse));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "게시글 단일 조회",
            description = "- 선택적 인증 처리되었습니다.",
            security = { @SecurityRequirement(name = "bearer-key") }
    )
    public ResponseEntity<ApiResponse<PostDto>> getPostById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = null;

        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            userId = userDetails.getId();
        }

        PostDto post = postService.findById(id, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글 조회가 완료되었습니다.", post));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "게시글 작성",
            security = { @SecurityRequirement(name = "bearer-key") }
    )
    public ResponseEntity<ApiResponse<PostDto>> createPost(
            @Valid @RequestBody PostCreateDto createDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostDto createdPost = postService.create(createDto, userDetails.getId());
        return new ResponseEntity<>(ApiResponse.success("게시글 작성이 완료되었습니다.", createdPost), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "게시글 수정",
            security = { @SecurityRequirement(name = "bearer-key") }
    )
    public ResponseEntity<ApiResponse<PostDto>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateDto updateDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostDto updatedPost = postService.update(id, updateDto, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("게시글 수정이 완료되었습니다.", updatedPost));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "게시글 삭제",
            security = { @SecurityRequirement(name = "bearer-key") }
    )
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.delete(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("게시글 삭제가 완료되었습니다."));
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "게시글 좋아요",
            security = { @SecurityRequirement(name = "bearer-key") }
    )
    public ResponseEntity<ApiResponse<Void>> likePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.likePost(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("게시글 좋아요가 완료되었습니다."));
    }

    @DeleteMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "게시글 좋아요 취소",
            security = { @SecurityRequirement(name = "bearer-key") }
    )
    public ResponseEntity<ApiResponse<Void>> unlikePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.unlikePost(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("게시글 좋아요 취소가 완료되었습니다."));
    }

    @GetMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "게시글 좋아요 여부 확인",
            security = { @SecurityRequirement(name = "bearer-key") }
    )
    public ResponseEntity<ApiResponse<Boolean>> hasUserLikedPost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean hasLiked = postService.hasUserLikedPost(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("게시글 좋아요 여부 확인이 완료되었습니다.", hasLiked));
    }
}
