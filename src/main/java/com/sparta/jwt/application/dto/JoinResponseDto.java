package com.sparta.jwt.application.dto;

import com.sparta.jwt.domain.RoleEnum;
import lombok.Builder;

import java.util.Set;

@Builder
public record JoinResponseDto(
        String username,
        String nickname,
        Set<RoleEnum> authorities
) {
}
