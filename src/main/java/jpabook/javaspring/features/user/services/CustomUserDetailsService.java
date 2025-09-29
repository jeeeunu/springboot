package jpabook.javaspring.features.user.services;

import jpabook.javaspring.features.user.domains.CustomUserDetails;
import jpabook.javaspring.features.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. email=" + email));
    }
    
    public CustomUserDetails loadUserById(Long id) throws UsernameNotFoundException {
        return userRepository.findById(id)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. id=" + id));
    }
}