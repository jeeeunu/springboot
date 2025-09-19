package jpabook.javaspring.features.auth.services;

import jpabook.javaspring.features.admin.dtos.AdminSummaryResponseDto;
import jpabook.javaspring.features.admin.services.AdminService;
import jpabook.javaspring.features.auth.dtos.AdminLoginDto;
import jpabook.javaspring.features.auth.dtos.TokenResponse;
import jpabook.javaspring.features.user.dtos.UserDto;
import jpabook.javaspring.features.auth.dtos.UserLoginDto;
import jpabook.javaspring.features.user.dtos.UserRegistrationDto;
import jpabook.javaspring.features.user.services.CustomUserDetailsService;
import jpabook.javaspring.features.user.services.UserService;
import jpabook.javaspring.security.JwtProvider;
import jpabook.javaspring.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AdminService adminService;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public UserDto register(UserRegistrationDto registrationDto) {
        return userService.register(registrationDto);
    }
//    public TokenResponse loginAdmin(AdminLoginDto loginDto) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginDto.getLoginId(), loginDto.getPassword())
//        );
//
//        // 인증에 성공한 Authentication 객체를 SecurityContextHolder에 저장 (전역 보안 컨텍스트에 로그인 사용자 정보 등록)
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        String jwt = jwtTokenProvider.generateToken(authentication);
//
//        AdminSummaryResponseDto userDto = adminService.findByloginId(loginDto.getLoginId());
//
//        return TokenResponse.of(jwt, userDto);
//    }

    public TokenResponse loginUser(UserLoginDto loginDto) {
        UserDetails ud = customUserDetailsService.loadUserByUsername(loginDto.getEmail());

        if (!passwordEncoder.matches(loginDto.getPassword(), ud.getPassword())) {
            throw new AuthenticationException("Invalid credentials") {
            };
        }

        // 권한은 ud.getAuthorities()에서 꺼내 claim 에 넣어두면 됨
        String token = jwtProvider.createAccessToken(ud.getUsername(),
                ud.getAuthorities()); // 구현에 맞게

        return TokenResponse.of(token);
    }
}