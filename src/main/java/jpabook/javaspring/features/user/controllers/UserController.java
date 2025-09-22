package jpabook.javaspring.features.user.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jpabook.javaspring.features.auth.services.AuthService;
import jpabook.javaspring.features.user.domains.CustomUserDetails;
import jpabook.javaspring.common.dto.ApiResponse;
import jpabook.javaspring.features.user.dtos.UserDto;
import jpabook.javaspring.features.user.dtos.UserRegistrationDto;
import jpabook.javaspring.features.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

//    @GetMapping("/me")
//    @PreAuthorize("isAuthenticated()")
//    @Operation(
//            summary = "내 정보 조회",
//            security = @SecurityRequirement(name = "bearer-key")
//    )
//    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        UserDto userDto = userService.findByUserId(userDetails.getId());
//        return ResponseEntity.ok(ApiResponse.success("현재 사용자 정보 조회가 완료되었습니다.", userDto));
//    }

    @PostMapping()
    @Operation(
            summary = "유저 생성"
    )
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        UserDto userDto = authService.register(registrationDto);
        return new ResponseEntity<>(ApiResponse.success("회원가입이 완료되었습니다.", userDto), HttpStatus.CREATED);
    }
}