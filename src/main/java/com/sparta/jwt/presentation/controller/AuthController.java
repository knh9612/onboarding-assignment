package com.sparta.jwt.presentation.controller;

import com.sparta.jwt.application.dto.request.JoinRequestDto;
import com.sparta.jwt.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "Auth Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "Auth API", description = "Auth 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> join(@RequestBody JoinRequestDto joinRequestDto) {
        return ResponseEntity.ok(authService.join(joinRequestDto));
    }
}
