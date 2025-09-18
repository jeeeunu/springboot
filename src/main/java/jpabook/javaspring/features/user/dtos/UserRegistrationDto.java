package jpabook.javaspring.features.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jpabook.javaspring.features.user.domains.Role;
import jpabook.javaspring.features.user.domains.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDto {

    @NotBlank(message = "사용자명을 입력해주세요")
    @Size(min = 4, max = 20, message = "사용자명은 4자에서 20자 사이여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "사용자명은 영문자, 숫자, 밑줄만 포함할 수 있습니다")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @NotBlank()
    @Email(message = "올바른 이메일 형식을 입력해주세요")
    @Schema(description = "이메일 주소", example = "string@example.com")
    private String email;
    
    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .password(passwordEncoder.encode(password))
                .name(name)
                .email(email)
                .role(Role.USER) // Default role for new users
                .build();
    }
}