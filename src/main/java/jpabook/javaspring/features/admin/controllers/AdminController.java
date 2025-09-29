package jpabook.javaspring.features.admin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jpabook.javaspring.common.dto.ApiResponse;
import jpabook.javaspring.features.admin.domains.CustomAdminDetails;
import jpabook.javaspring.features.admin.dtos.AdminCreateDto;
import jpabook.javaspring.features.admin.dtos.AdminSummaryResponseDto;
import jpabook.javaspring.features.admin.services.AdminService;
import jpabook.javaspring.features.post.dtos.PostCreateDto;
import jpabook.javaspring.features.post.dtos.PostDto;
import jpabook.javaspring.features.user.domains.CustomUserDetails;
import jpabook.javaspring.features.user.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자 관리 API")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MANAGER')")
    @Operation(
            summary = "내 정보 조회",
            security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<ApiResponse<AdminSummaryResponseDto>> getCurrentUser(
            @AuthenticationPrincipal CustomAdminDetails adminDetail) {
        AdminSummaryResponseDto adminDto = adminService.findByLoginId(adminDetail.getLoginId());
        return ResponseEntity.ok(ApiResponse.success("현재 사용자 정보 조회가 완료되었습니다.", adminDto));
    }


    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "관리자 생성",
            security = { @SecurityRequirement(name = "bearer-key") }
    )
    public ResponseEntity<ApiResponse<AdminSummaryResponseDto>> createAdmin(
            @Valid @RequestBody AdminCreateDto createDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AdminSummaryResponseDto createdPost = adminService.create(createDto);
        return new ResponseEntity<>(ApiResponse.success("관리자 생성이 완료되었습니다.", createdPost), HttpStatus.CREATED);
    }
}
