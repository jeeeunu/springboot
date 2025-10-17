package jpabook.javaspring.config;

import jpabook.javaspring.features.admin.domains.Admin;
import jpabook.javaspring.features.admin.domains.AdminRole;
import jpabook.javaspring.features.admin.repositories.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class SeedConfig {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedSuperAdmin() {
        return args -> {
            if (!adminRepository.existsByLoginId("superadmin")) {
                adminRepository.save(Admin.builder()
                        .loginId("superadmin")
                        .password(passwordEncoder.encode("change-me-1234"))
                        .name("슈퍼관리자")
                        .role(AdminRole.SUPER_ADMIN)
                        .build());
                System.out.println("✅ SuperAdmin 계정이 생성되었습니다 (loginId=superadmin).");
            }

            Optional<Admin> superAdminOpt = adminRepository.findByLoginId("superadmin");
            if (superAdminOpt.isPresent()) {
                Admin superAdmin = superAdminOpt.get();
                if (passwordEncoder.matches("change-me-1234", superAdmin.getPassword())) {
                    System.err.println("⚠️ [경고] superadmin 계정의 비밀번호가 아직 기본값입니다. 변경 부탁드립니다.");
                }
            }
        };
    }
}
