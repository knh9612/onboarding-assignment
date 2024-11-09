package com.sparta.jwt.application.dto.response;

import com.sparta.jwt.application.dto.RoleDto;
import lombok.Builder;

@Builder
public record JoinResponseDto(
        String username,
        String nickname,
        RoleDto authorities
) {
}
