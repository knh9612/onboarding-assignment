package com.sparta.jwt.presentation.controller;

import com.sparta.jwt.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "User Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User API", description = "User 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 정보 조회", description = "본인 정보 조회 API")
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<?> getUserInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }
}
