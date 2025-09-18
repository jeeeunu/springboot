package jpabook.javaspring.features.auth.services;

import jpabook.javaspring.features.auth.dtos.TokenResponse;
import jpabook.javaspring.features.user.dtos.UserDto;
import jpabook.javaspring.features.user.dtos.UserLoginDto;
import jpabook.javaspring.features.user.dtos.UserRegistrationDto;
import jpabook.javaspring.features.user.services.UserService;
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