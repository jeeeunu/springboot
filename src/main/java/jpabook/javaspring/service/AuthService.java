package jpabook.javaspring.service;

import jpabook.javaspring.dto.auth.TokenResponse;
import jpabook.javaspring.dto.user.UserDto;
import jpabook.javaspring.dto.user.UserLoginDto;
import jpabook.javaspring.dto.user.UserRegistrationDto;
import jpabook.javaspring.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);

        UserDto userDto = userService.findByUsername(loginDto.getUsername());

        return TokenResponse.of(jwt, userDto);
    }
}