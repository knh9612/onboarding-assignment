package com.sparta.jwt.application.service;

import com.sparta.jwt.application.dto.request.JoinRequestDto;
import com.sparta.jwt.application.dto.response.UserResponseDto;
import com.sparta.jwt.application.service.util.UserMapper;
import com.sparta.jwt.domain.model.User;
import com.sparta.jwt.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j(topic = "Auth Service")
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto join(JoinRequestDto joinRequestDto) {
        // username 중복 체크
        if (userRepository.existsByUsername(joinRequestDto.username())) {
            log.error("username already exists");
            // Todo: 커스텀 예외
            throw new RuntimeException("이미 존재");
        }
        // Default 권한을 User로 설정
        // Todo: 추가 요구 사항이 있으면 그에 맞는 권한 로직 추가
        User user = userRepository.save(UserMapper.entityFrom(joinRequestDto.withPassword(passwordEncoder.encode(joinRequestDto.password()))));

        return UserMapper.userResponseDtoFrom(user);
    }

}
