package com.sparta.jwt.infrastructure.security.filter;

import com.sparta.jwt.application.service.util.CacheUtil;
import com.sparta.jwt.application.service.util.UserMapper;
import com.sparta.jwt.infrastructure.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j(topic = "Logout Filter")
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final CacheUtil cacheUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        // 로그아웃 요청이 아니거나 POST요청이 아니면 다음 필터로..
        if (!requestURI.matches("/logout") || !requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }
        // refresh 토큰 가져오기
        String refreshTokenFromCookie = jwtUtil.getRefreshTokenFromCookie(request);
        String username = jwtUtil.getUsernameFromRefreshToken(refreshTokenFromCookie);
        String userRole = jwtUtil.getUserRoleFromRefreshToken(refreshTokenFromCookie);

        String accessTokenValue = jwtUtil.getAccessTokenValue(jwtUtil.getAccessTokenFromHeader(request));


        // null이거나 존재하지 않는 refreshToken이면 예외
        if (refreshTokenFromCookie == null || cacheUtil.getValidRefreshToken(username) == null) {
            log.error("존재하지 않는 refresh Token");
            throw new RuntimeException("존재하지 않는 refresh Token");
        }

        // 존재하고, 만료되지 않은 refreshToken이면 화이트리스트에서 삭제 처리, accessToken 블랙리스트 처리
        if (jwtUtil.isValidateRefreshToken(refreshTokenFromCookie)) {
            cacheUtil.deleteRefreshToken(username);
            cacheUtil.setAccessTokenToBlackList(accessTokenValue, UserMapper.userInfoFrom(username, userRole, accessTokenValue));

            Cookie cookie = new Cookie(jwtUtil.COOKIE_NAME, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");

            response.addCookie(cookie);
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
