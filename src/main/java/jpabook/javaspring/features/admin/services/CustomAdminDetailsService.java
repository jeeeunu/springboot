//package jpabook.javaspring.features.admin.services;
//
//import jpabook.javaspring.features.admin.domains.CustomAdminDetails;
//import jpabook.javaspring.features.admin.repositories.AdminRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class CustomAdminDetailsService implements UserDetailsService {
//
//    private final AdminRepository adminRepository;
//
//    @Override
//    public CustomAdminDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
//        return adminRepository.findByLoginId(loginId)
//                .map(CustomAdminDetails::new)
//                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. loginId=" + loginId));
//    }
//
//    public CustomAdminDetails loadUserById(Long id) throws UsernameNotFoundException {
//        return adminRepository.findById(id)
//                .map(CustomAdminDetails::new)
//                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. id=" + id));
//    }
//}