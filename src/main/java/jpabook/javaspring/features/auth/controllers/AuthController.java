package jpabook.javaspring.features.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jpabook.javaspring.features.auth.dtos.TokenResponse;
import jpabook.javaspring.common.dto.ApiResponse;
import jpabook.javaspring.features.user.dtos.UserDto;
import jpabook.javaspring.features.user.dtos.UserLoginDto;
import jpabook.javaspring.features.user.dtos.UserRegistrationDto;
import jpabook.javaspring.features.auth.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "사용자 관리")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "회원가입"
    )
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        UserDto userDto = authService.register(registrationDto);
        return new ResponseEntity<>(ApiResponse.success("회원가입이 완료되었습니다.", userDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인"
    )
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody UserLoginDto loginDto) {
        TokenResponse tokenResponse = authService.login(loginDto);
        return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", tokenResponse));
    }
}
