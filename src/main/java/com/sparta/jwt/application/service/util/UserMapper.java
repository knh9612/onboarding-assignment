package com.sparta.jwt.application.service.util;

import com.sparta.jwt.application.dto.request.JoinRequestDto;
import com.sparta.jwt.application.dto.response.UserResponseDto;
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

    public static UserResponseDto userResponseDtoFrom(User user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .authorities(RoleMapper.toDto(user.getAuthorities()))
                .build();
    }
}
