package jpabook.javaspring.features.admin.domains;

import jpabook.javaspring.features.admin.domains.Admin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomAdminDetails implements UserDetails {
    private final Admin admin;

    public CustomAdminDetails(Admin admin) {
        this.admin = admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + admin.getRole().name()));
    }

    public Long getId() {
        return admin.getId();
    }

    public String getLoginId() {
        return admin.getLoginId();
    }

    public AdminRole getRole() {
        return admin.getRole();
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }


    @Override
    public String getUsername() {
        return admin.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return admin.getDeletedAt() == null;
    }

    public Admin getAdmin() {
        return admin;
    }
}