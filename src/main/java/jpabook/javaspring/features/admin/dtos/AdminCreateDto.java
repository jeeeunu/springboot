package jpabook.javaspring.features.admin.dtos;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank()
    private AdminRole role;

    public Admin toEntity(PasswordEncoder passwordEncoder) {
        return Admin.builder()
                .password(passwordEncoder.encode(password))
                .loginId(loginId)
                .role(role)
                .build();
    }
}
