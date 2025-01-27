package com.sparta.jwt.infrastructure.security;

import com.sparta.jwt.domain.RoleEnum;
import com.sparta.jwt.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public User getUser() {
        return user;
    }

    // 사용자의 권한 정보를 반환하여, spring security의 권한 검증 과정에서 사용
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        RoleEnum role = user.getAuthorities();
        String authority = role.getAuthority();

        // 한 사용자는 하나의 권한만 가질 수 있도록
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
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
        return true;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
}
