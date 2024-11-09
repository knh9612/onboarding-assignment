package com.sparta.jwt.infrastructure.security;

import com.sparta.jwt.domain.model.User;
import com.sparta.jwt.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j(topic = "UserDetails Service")
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // user정보를 확인하고 UserDetailsImpl 생성자로 보내서 UserDetailsImpl을 반환
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new RuntimeException("해당 유저를 찾을 수 없음")
        );

        return new UserDetailsImpl(user);
    }
}

