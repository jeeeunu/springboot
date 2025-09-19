package jpabook.javaspring.features.admin.services;

import jpabook.javaspring.features.admin.domains.Admin;
import jpabook.javaspring.features.admin.dtos.AdminCreateDto;
import jpabook.javaspring.features.admin.dtos.AdminSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final PasswordEncoder passwordEncoder;

    public AdminSummaryResponseDto create(AdminCreateDto createDto) {

        AdminSummaryResponseDto admin = createDto.toEntity(passwordEncoder);

        return admin;
    }

    public AdminSummaryResponseDto findByloginId(String loginId) {
        Admin admin = Admin.builder()
                .loginId(loginId)
                .build();
        return AdminSummaryResponseDto.fromEntity(admin);
    }
}
