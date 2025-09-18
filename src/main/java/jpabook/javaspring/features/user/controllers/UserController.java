package jpabook.javaspring.features.user.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.javaspring.features.user.domains.CustomUserDetails;
import jpabook.javaspring.common.dto.ApiResponse;
import jpabook.javaspring.features.user.dtos.UserDto;
import jpabook.javaspring.features.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "내 정보 조회",
            security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserDto userDto = userService.findByUserId(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("현재 사용자 정보 조회가 완료되었습니다.", userDto));
    }
}