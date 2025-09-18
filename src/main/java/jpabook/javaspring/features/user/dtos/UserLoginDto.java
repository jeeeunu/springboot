package jpabook.javaspring.features.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginDto {
    @NotBlank()
    @Schema(example = "string@example.com")
    private String email;

    @NotBlank()
    private String password;
}
