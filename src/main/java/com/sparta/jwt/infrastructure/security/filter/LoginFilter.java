package com.sparta.jwt.infrastructure.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.jwt.application.dto.request.LoginRequestDto;
import com.sparta.jwt.application.dto.response.LoginResponseDto;
import com.sparta.jwt.application.service.util.CacheUtil;
import com.sparta.jwt.application.service.util.UserMapper;
import com.sparta.jwt.infrastructure.security.UserDetailsImpl;
import com.sparta.jwt.infrastructure.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "Login Filter")
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final CacheUtil cacheUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDto login = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
            log.info(login.username());

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            login.username(),
                            login.password(),
                            null // 권한 정보인데, 인증 요청을 처리하는 데에는 필요하지 않음. 권한 정보는 인증 요청이 성공하면 Authentication객체를 통해 반환
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getUser().getAuthorities().getAuthority();

        String accessToken = jwtUtil.createAccessToken(username, role);
        String refreshToken = jwtUtil.createRefreshToken(username, role);

        cacheUtil.saveRefreshToken(username, UserMapper.userInfoFrom(username, role, refreshToken));

        response.addHeader(jwtUtil.AUTHORIZATION_HEADER, accessToken);
        response.addCookie(jwtUtil.createCookieWithRefreshToken(refreshToken));

        // JSON 형태의 응답 데이터 작성
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 응답 바디에 JSON 형태로 성공 메시지와 토큰 추가
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(new LoginResponseDto(accessToken))
        );
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.error("Unsuccessful Authentication: {}", failed.getMessage());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
