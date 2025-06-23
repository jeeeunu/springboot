package jpabook.javaspring.controller;

import jpabook.javaspring.dto.common.ApiResponse;
import jpabook.javaspring.dto.user.UserDto;
import jpabook.javaspring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(ApiResponse.success("사용자 목록 조회가 완료되었습니다.", users));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserDto userDto = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("현재 사용자 정보 조회가 완료되었습니다.", userDto));
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByUsername(@PathVariable String username) {
        UserDto userDto = userService.findByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회가 완료되었습니다.", userDto));
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String username) {
        userService.deleteByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("사용자 삭제가 완료되었습니다."));
    }
}
