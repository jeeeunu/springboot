package jpabook.javaspring.controller;

import jakarta.validation.Valid;
import jpabook.javaspring.dto.common.ApiResponse;
import jpabook.javaspring.dto.user.UserDto;
import jpabook.javaspring.dto.user.UserLoginDto;
import jpabook.javaspring.dto.user.UserRegistrationDto;
import jpabook.javaspring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        UserDto userDto = userService.register(registrationDto);
        return new ResponseEntity<>(ApiResponse.success("회원가입이 완료되었습니다.", userDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@Valid @RequestBody UserLoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDto userDto = userService.findByUsername(loginDto.getUsername());
        return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", userDto));
    }
}
