package jpabook.javaspring.features.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jpabook.javaspring.features.auth.dtos.AdminLoginDto;
import jpabook.javaspring.features.auth.dtos.TokenResponse;
import jpabook.javaspring.common.dto.ApiResponse;
import jpabook.javaspring.features.auth.dtos.UserLoginDto;
import jpabook.javaspring.features.auth.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "사용자 관리")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/admins")
    @Operation(
            summary = "관리자 로그인"
    )
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody AdminLoginDto loginDto) {
        TokenResponse tokenResponse = authService.loginAdmin(loginDto);
        return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", tokenResponse));
    }

    @PostMapping("/users")
    @Operation(
            summary = "유저 로그인"
    )
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody UserLoginDto loginDto) {
        TokenResponse tokenResponse = authService.loginUser(loginDto);
        return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", tokenResponse));
    }
}
