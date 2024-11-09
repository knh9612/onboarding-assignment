package com.sparta.jwt.application.dto.request;

import lombok.With;

public record JoinRequestDto(
        String username,
        @With String password,
        String nickname
) {
}
