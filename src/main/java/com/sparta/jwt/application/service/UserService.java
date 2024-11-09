package com.sparta.jwt.application.service;

import com.sparta.jwt.application.dto.response.UserResponseDto;
import com.sparta.jwt.application.service.util.UserMapper;
import com.sparta.jwt.domain.model.User;
import com.sparta.jwt.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j(topic = "User Service")
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto getUserInfo() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username " + username + " not found")
        );

        return UserMapper.userResponseDtoFrom(user);

    }
}
