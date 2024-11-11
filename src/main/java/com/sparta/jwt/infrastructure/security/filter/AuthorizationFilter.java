package com.sparta.jwt.infrastructure.security.filter;

import com.sparta.jwt.application.dto.ReissuedTokenDto;
import com.sparta.jwt.application.dto.UserInfoDto;
import com.sparta.jwt.application.service.util.CacheUtil;
import com.sparta.jwt.application.service.util.UserMapper;
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
    private final CacheUtil cacheUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 예외 경로 설정
        String requestURI = request.getRequestURI();
        log.info("requestURI: {}", requestURI);
        if (requestURI.equals("/signup") || requestURI.equals("/sign") || requestURI.startsWith("/swagger-ui") || requestURI.startsWith("/v3/api-docs")) {
            // 회원가입, 로그인 요청은 필터를 거치지 않고 다음 필터로 바로 이동
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);

        if (accessToken == null || refreshToken == null) {
            log.error("토큰이 존재하지 않음");
            throw new RuntimeException("토큰이 존재하지 않음");
        }

        if (cacheUtil.getBlackAccessToken(accessToken) != null) {
            log.error("로그아웃 된 사용자. 유효하지 않은 토큰");
            throw new RuntimeException("로그아웃 된 사용자. 유효하지 않은 토큰");
        }

        // 유효한 토큰이면 Authentication 객체 생성
        // 만료된 토큰이면, Refresh Token 검증 후 재발급
        // 유효하지 않은 토큰이면 예외처리
        if (!jwtUtil.isValidateAccessToken(accessToken)) {
            log.info("Refresh Token으로 토큰 재발급");
            accessToken = reissue(refreshToken).accessToken();
            refreshToken = reissue(accessToken).refreshToken();

            response.addHeader(jwtUtil.AUTHORIZATION_HEADER, accessToken);
            response.addCookie(jwtUtil.createCookieWithRefreshToken(refreshToken));
        }
        // 인증 처리
        String username = jwtUtil.getUsernameFromAccessToken(accessToken);
        String userRole = jwtUtil.getUserRoleFromAccessToken(accessToken);
        setAuthentication(username, userRole);

        filterChain.doFilter(request, response);
    }

    // 인증 처리
    public void setAuthentication(String username, String userRole) {
        User user = new User(username, "", Collections.singletonList(new SimpleGrantedAuthority(userRole)));
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 토큰 재발급 메서드
    public ReissuedTokenDto reissue(String refreshToken) {
        String username = jwtUtil.getUsernameFromRefreshToken(refreshToken);
        String userRole = jwtUtil.getUserRoleFromRefreshToken(refreshToken);

        UserInfoDto validRefreshToken = cacheUtil.getValidRefreshToken(username);
        if (validRefreshToken == null) {
            log.error("Refresh Token이 존재하지 않음");
            throw new RuntimeException("Refresh Token이 존재하지 않음");

        } else {
            jwtUtil.isValidateRefreshToken(refreshToken);
        }

        String reissuedAccess = jwtUtil.createAccessToken(username, userRole);
        String reissuedRefresh = jwtUtil.createRefreshToken(username, userRole);

        cacheUtil.saveRefreshToken(username, UserMapper.userInfoFrom(username, userRole, reissuedRefresh));

        return new ReissuedTokenDto(reissuedAccess, reissuedRefresh);
    }

}
