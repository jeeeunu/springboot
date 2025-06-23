package jpabook.javaspring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.javaspring.dto.common.ApiResponse;
import jpabook.javaspring.dto.user.UserDto;
import jpabook.javaspring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin/Users", description = "사용자 관리 API")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "모든 사용자 조회 (Admin용)",
            security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(ApiResponse.success("사용자 목록 조회가 완료되었습니다.", users));
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "사용자 삭제 (Admin용)",
            security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String username) {
        userService.deleteByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("사용자 삭제가 완료되었습니다."));
    }
}