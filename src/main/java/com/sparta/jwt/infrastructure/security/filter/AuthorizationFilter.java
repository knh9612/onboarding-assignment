package com.sparta.jwt.infrastructure.security.filter;

import com.sparta.jwt.infrastructure.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j(topic = "Authorization Filter")
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 예외 경로 설정
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/auth/join") || requestURI.equals("/api/auth/login")) {
            // 회원가입, 로그인 요청은 필터를 거치지 않고 다음 필터로 바로 이동
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        System.out.println("accessToken 1 = " + accessToken);
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);

        if (accessToken == null || refreshToken == null) {
            log.error("헤더에 Access 토큰이 존재하지 않음");
            throw new RuntimeException("헤더에 Access 토큰이 존재하지 않음");
        }

        // 유효한 토큰이면 Authentication 객체 생성
        // 만료된 토큰이면, Refresh Token 검증 후 재발급
        // 유효하지 않은 토큰이면 예외처리
        if (!jwtUtil.isValidateAccessToken(accessToken)) {
            if (jwtUtil.isValidateRefreshToken(refreshToken)) {
                String username = jwtUtil.getUsernameFromRefreshToken(refreshToken);
                System.out.println("username = " + username);
                String userRole = jwtUtil.getUserRoleFromRefreshToken(refreshToken);
                System.out.println("userRole = " + userRole);
                accessToken = jwtUtil.createAccessToken(username, userRole);
                System.out.println("accessToken 2 = " + accessToken);
                refreshToken = jwtUtil.createRefreshToken(username, userRole);
                System.out.println("refreshToken = " + refreshToken);

                response.addHeader(jwtUtil.AUTHORIZATION_HEADER, accessToken);
                response.addCookie(jwtUtil.createCookieWithRefreshToken(refreshToken));
            }
        }
        // 인증 처리
        String username = jwtUtil.getUsernameFromAccessToken(accessToken);
        System.out.println("username = " + username);
        String userRole = jwtUtil.getUserRoleFromAccessToken(accessToken);
        System.out.println("userRole = " + userRole);
        setAuthentication(username, userRole);

        filterChain.doFilter(request, response);
    }

    // 인증 처리
    public void setAuthentication(String username, String userRole) {
        User user = new User(username, "", Collections.singletonList(new SimpleGrantedAuthority(userRole)));
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
