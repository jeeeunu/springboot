package jpabook.javaspring.features.admin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jpabook.javaspring.common.dto.ApiResponse;
import jpabook.javaspring.features.admin.dtos.AdminCreateDto;
import jpabook.javaspring.features.admin.dtos.AdminSummaryResponseDto;
import jpabook.javaspring.features.admin.services.AdminService;
import jpabook.javaspring.features.post.dtos.PostCreateDto;
import jpabook.javaspring.features.post.dtos.PostDto;
import jpabook.javaspring.features.user.domains.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자 관리 API")
public class AdminController {

    private final AdminService adminService;

    @PostMapping()
    @Operation(
            summary = "관리자 생성",
            security = { @SecurityRequirement(name = "bearer-key") }
    )
    public ResponseEntity<ApiResponse<AdminSummaryResponseDto>> createAdmin(
            @Valid @RequestBody AdminCreateDto createDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AdminSummaryResponseDto createdPost = adminService.create(createDto);
        return new ResponseEntity<>(ApiResponse.success("게시글 작성이 완료되었습니다.", createdPost), HttpStatus.CREATED);
    }
}
