package jpabook.javaspring.features.admin.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jpabook.javaspring.features.admin.domains.Admin;
import jpabook.javaspring.features.admin.domains.AdminRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCreateDto {
    @NotBlank()
    private String loginId;

    @NotBlank()
    private String password;

    @NotNull(message = "역할은 필수 값입니다.")
    private AdminRole role;

    public AdminSummaryResponseDto toEntity(PasswordEncoder passwordEncoder) {
        return AdminSummaryResponseDto.fromEntity(Admin.builder()
                .password(passwordEncoder.encode(password))
                .loginId(loginId)
                .role(role)
                .build());
    }
}
