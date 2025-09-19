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
public class TokenResponse<T> {
    private String accessToken;
    private String tokenType;
    private T authUser;

    public static <T> TokenResponse<T> of(String accessToken, T userDto) {
        return TokenResponse.<T>builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .authUser(userDto)
                .build();
    }
}