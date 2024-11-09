package com.sparta.jwt.application.service.util;

import com.sparta.jwt.application.dto.RoleDto;
import com.sparta.jwt.domain.RoleEnum;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public static RoleDto toDto(RoleEnum role) {
        return RoleDto.builder()
                .authorityName(role.getAuthority())
                .build();
    }
}
