package jpabook.javaspring.service;

import jpabook.javaspring.dto.auth.TokenResponse;
import jpabook.javaspring.dto.user.UserDto;
import jpabook.javaspring.dto.user.UserLoginDto;
import jpabook.javaspring.dto.user.UserRegistrationDto;
import jpabook.javaspring.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Transactional
    public UserDto register(UserRegistrationDto registrationDto) {
        return userService.register(registrationDto);
    }

    public TokenResponse login(UserLoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        // 인증에 성공한 Authentication 객체를 SecurityContextHolder에 저장 (전역 보안 컨텍스트에 로그인 사용자 정보 등록)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);

        UserDto userDto = userService.findByEmail(loginDto.getEmail());

        return TokenResponse.of(jwt, userDto);
    }
}