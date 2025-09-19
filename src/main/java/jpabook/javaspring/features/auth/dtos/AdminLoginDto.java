package jpabook.javaspring.features.auth.dtos;

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
public class AdminLoginDto {
    @NotBlank()
    @Schema(
        example = "superadmin"
    )
    private String loginId;

    @NotBlank()
    @Schema(
            example = "change-me-1234"
    )
    private String password;
}
