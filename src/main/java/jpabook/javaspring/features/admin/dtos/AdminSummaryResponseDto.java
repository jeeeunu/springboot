package jpabook.javaspring.features.admin.dtos;
import jpabook.javaspring.features.admin.domains.Admin;
import jpabook.javaspring.features.admin.domains.AdminRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSummaryResponseDto {
    private Long id;
    private String loginId;
    private String name;
    private AdminRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AdminSummaryResponseDto fromEntity(Admin admin) {
        return AdminSummaryResponseDto.builder()
                .id(admin.getId())
                .loginId(admin.getLoginId())
                .name(admin.getName())
                .role(admin.getRole())
                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())
                .build();
    }

}
