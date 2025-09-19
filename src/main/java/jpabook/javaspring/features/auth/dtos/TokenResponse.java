package jpabook.javaspring.features.auth.dtos;

import jpabook.javaspring.features.admin.dtos.AdminSummaryResponseDto;
import jpabook.javaspring.features.user.dtos.UserDto;
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

    public static TokenResponse of(String accessToken) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .build();
    }
}