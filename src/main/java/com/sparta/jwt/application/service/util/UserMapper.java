package com.sparta.jwt.application.service.util;

import com.sparta.jwt.application.dto.JoinRequestDto;
import com.sparta.jwt.application.dto.JoinResponseDto;
import com.sparta.jwt.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static User entityFrom(JoinRequestDto joinRequestDto) {
        return User.builder()
                .username(joinRequestDto.username())
                .nickname(joinRequestDto.nickname())
                .password(joinRequestDto.password())
                .build();
    }

    public static JoinResponseDto joinResponseDtoFrom(User user) {
        return JoinResponseDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .authorities(user.getAuthorities())
                .build();
    }
}
