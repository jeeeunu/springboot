package jpabook.javaspring.features.auth.services;

import jpabook.javaspring.features.admin.domains.Admin;
import jpabook.javaspring.features.admin.domains.CustomAdminDetails;
import jpabook.javaspring.features.admin.services.CustomAdminDetailsService;
import jpabook.javaspring.features.auth.dtos.AdminLoginDto;
import jpabook.javaspring.features.auth.dtos.TokenResponse;
import jpabook.javaspring.features.user.domains.CustomUserDetails;
import jpabook.javaspring.features.user.domains.User;
import jpabook.javaspring.features.user.dtos.UserDto;
import jpabook.javaspring.features.auth.dtos.UserLoginDto;
import jpabook.javaspring.features.user.dtos.UserRegistrationDto;
import jpabook.javaspring.features.user.services.CustomUserDetailsService;
import jpabook.javaspring.features.user.services.UserService;
import jpabook.javaspring.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
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
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAdminDetailsService customAdminDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserDto register(UserRegistrationDto registrationDto) {
        return userService.register(registrationDto);
    }

    public TokenResponse loginAdmin(AdminLoginDto loginDto) {
        CustomAdminDetails admin = customAdminDetailsService.loadUserByUsername(loginDto.getLoginId());

        if (!passwordEncoder.matches(loginDto.getPassword(), admin.getPassword())) {
            throw new AuthenticationException("패스워드가 일치하지 않습니다.") {
            };
        }

        String token = jwtTokenProvider.generateTokenForAdmin(admin);

        return TokenResponse.of(token);
    }

    public TokenResponse loginUser(UserLoginDto loginDto) {
        CustomUserDetails user = customUserDetailsService.loadUserByUsername(loginDto.getEmail());

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new AuthenticationException("패스워드가 일치하지 않습니다.") {
            };
        }

        String token = jwtTokenProvider.generateTokenForUser(user);

        return TokenResponse.of(token);
    }
}