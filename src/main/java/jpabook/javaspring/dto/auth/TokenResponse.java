package jpabook.javaspring.dto.auth;

import jpabook.javaspring.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {
    private String accessToken;
    private String tokenType;
    private UserDto user;
    
    public static TokenResponse of(String accessToken, UserDto userDto) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .user(userDto)
                .build();
    }
}