package jpabook.javaspring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.javaspring.dto.common.ApiResponse;
import jpabook.javaspring.dto.user.UserDto;
import jpabook.javaspring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @AuthenticationPrincipal UserDetails userDetails) {
        UserDto userDto = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("현재 사용자 정보 조회가 완료되었습니다.", userDto));
    }

    @GetMapping("/{username}")
    @Operation(
            summary = "사용자 정보 조회 (Public)",
            security = {} // 오픈 API
    )
    public ResponseEntity<ApiResponse<UserDto>> getUserByUsername(@PathVariable String username) {
        UserDto userDto = userService.findByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회가 완료되었습니다.", userDto));
    }
}