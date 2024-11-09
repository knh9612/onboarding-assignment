package com.sparta.jwt.infrastructure.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j(topic = "JWT Utility")
@Component
public class JwtUtil {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 id 값의 KEY
    public static final String AUTHENTICATION_KEY = "userId";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // Refresh Token 만료기간
    public static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일
    // Refresh Token을 담는 Cookie 이름
    public static final String COOKIE_NAME = "RefreshTokenCookie";
    // Access Token 만료기간
    private static final long ACCESS_TOKEN_TIME = 10 * 60 * 1000L; // 10분
    private SecretKey secretKey;
    private SecretKey refreshSecretKey;

    /**
     * .env파일에 있는 키를 암호화해서 SecretKey객체 타입으로 저장
     * 양방향 암호화의 대칭키 방식 사용: 동일한 방식으로 암호화, 복호화 진행. cf. 비대칭키 방식으로도 가능
     */
    public JwtUtil(@Value("${jwt.secret.key}") String secret, @Value("${jwt.refresh.secret.key}") String refresh) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.refreshSecretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(String userId, String role) {
        return BEARER_PREFIX +
                Jwts.builder()
                        .claim(AUTHENTICATION_KEY, userId)
                        .claim(AUTHORIZATION_KEY, role)
                        .issuedAt(new Date(System.currentTimeMillis()))
                        .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_TIME))
                        .signWith(secretKey)
                        .compact();
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(String userId, String role) {
        return Jwts.builder()
                .claim(AUTHENTICATION_KEY, userId)
                .claim(AUTHORIZATION_KEY, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME))
                .signWith(refreshSecretKey)
                .compact();
    }

    /**
     * RefreshToken 쿠키에 담기
     */
    public Cookie createCookieWithRefreshToken(String refreshToken) {
        // Cookie Value에는 공백이 불가능하므로 encoding 진행
        try {
            refreshToken = URLEncoder.encode(refreshToken, "utf-8").replaceAll("\\+", "%20");
            Cookie refreshTokenCookie = new Cookie(COOKIE_NAME, refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge((int) (REFRESH_TOKEN_TIME / 1000)); // 밀리 초가 아닌 초 단위기 때문에 /1000
            return refreshTokenCookie;
        } catch (UnsupportedEncodingException e) {
            log.error("Refresh Token encoding 오류: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Access 토큰 검증하는 메서드
     *
     * @param accessToken
     * @return
     */
    public boolean isValidateAccessToken(String accessToken) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(accessToken);
            return true;

            // Todo: 커스텀 예외
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token, 만료된 Access 토큰입니다.");
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Access: Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            throw new RuntimeException("지원하지 않는 토큰");
        } catch (MalformedJwtException | io.jsonwebtoken.security.SecurityException e) {
            log.error("Access: Invalid JWT token, 유효하지 않은 JWT token 입니다.");
            throw new RuntimeException("유효하지 않은 토큰");
        } catch (IllegalArgumentException e) {
            log.error("Access: JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            throw new RuntimeException("잘못된 토큰");
        }
    }

    /**
     * Access 토큰 검증하는 메서드
     *
     * @param refreshToken
     * @return
     */
    public boolean isValidateRefreshToken(String refreshToken) {
        try {
            Jwts.parser().verifyWith(refreshSecretKey).build().parseSignedClaims(refreshToken);
            return true;

            // Todo: 커스텀 예외
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token, 만료된 Refresh 토큰입니다.");
            throw new RuntimeException("만료된 refresh token 다시 로그인.");
        } catch (UnsupportedJwtException e) {
            log.error("Refresh: Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            throw new RuntimeException("지원되지 않는 토큰");
        } catch (MalformedJwtException | SecurityException e) {
            log.error("Refresh: Invalid JWT token, 유효하지 않은 JWT token 입니다.");
            throw new RuntimeException("유효하지 않은 토큰");
        } catch (IllegalArgumentException e) {
            log.error("Refresh: JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            throw new RuntimeException("잘못된 토큰");
        }
    }

    // Access 토큰에서 사용자 id 가져오기
    public String getUserIdFromAccessToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get(AUTHENTICATION_KEY, String.class);
    }

    // Access 토큰에서 사용자 role 가져오기
    public String getUserRoleFromAccessToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get(AUTHORIZATION_KEY, String.class);
    }

    // Refresh 토큰에서 사용자 정보 가져오기
    public String getUserIdFromRefreshToken(String token) {
        return Jwts.parser().verifyWith(refreshSecretKey).build().parseSignedClaims(token).getPayload().get(AUTHENTICATION_KEY, String.class);
    }

    public String getUserRoleFromRefreshToken(String token) {
        return Jwts.parser().verifyWith(refreshSecretKey).build().parseSignedClaims(token).getPayload().get(AUTHORIZATION_KEY, String.class);
    }

}
