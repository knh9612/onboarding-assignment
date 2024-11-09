package com.sparta.jwt.infrastructure.security.jwt;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        // JwtUtil 객체 초기화
        jwtUtil = new JwtUtil("asdfassdfdsfdasfsadfsasdfASDFSDF3dafsdadfd", "asdfa3232fsdfsdfsadfsdfsadfasdfdsfsdfkl");
    }

    @Test
    @DisplayName("토큰 발급 테스트")
    public void testGenerateAccessTokenAndRefreshToken() {
        // 로그인 후 반환된 사용자 정보
        String username = "testUser";
        String role = "ROLE_USER";

        // JWT 토큰 생성 메서드 호출
        String accessToken = jwtUtil.createAccessToken(username, role);
        String refreshToken = jwtUtil.createRefreshToken(username, role);

        // 발급된 토큰이 null이 아니고, "Bearer"로 시작하는지 확인
        assertThat(accessToken, notNullValue());
        assertThat(accessToken, containsString("Bearer"));

        assertThat(refreshToken, notNullValue());
        assertThat(refreshToken, notNullValue());
    }

    @Test
    @DisplayName("유효성 검사 성공 시나리오")
    public void testTokenValidationWithCorrectKey() {
        // 시나리오: 올바른 키로 생성된 토큰의 유효성 검사 성공
        String username = "testUser";
        String role = "ROLE_USER";

        // 1. 올바른 키로 Access Token 생성
        String accessToken = jwtUtil.createAccessToken(username, role);

        // 2. 생성된 토큰이 null이 아님을 검증
        assertThat("Access token should not be null", accessToken, notNullValue());

        // 3. 토큰의 유효성 검증
        boolean isValid = jwtUtil.isValidateAccessToken(accessToken);

        // 4. 올바른 키로 생성된 토큰이므로 true 반환
        assertTrue(isValid, "Token should be valid with correct signing key");
    }

    @Test
    @DisplayName("다른 SecretKey로 토큰 생성 시 유효성 검사 실패 시나리오")
    public void testTokenValidationWithIncorrectKey() {
        // 시나리오: 다른 키로 생성된 토큰의 유효성 검사 실패
        String username = "testUser";
        String role = "ROLE_USER";

        // 1. JwtUtil과는 다른 키로 토큰 생성
        String incorrectKey = "differentSecretKeyasdfasdfjasd;lkfj23r09jsd0clkASL;KDFJL;SADJFL23ASDF";
        String invalidToken = Jwts.builder()
                .claim("userId", username)
                .claim("auth", role)
                .signWith(new SecretKeySpec(incorrectKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm()))
                .compact();

        // 2. 생성된 토큰이 null이 아님을 검증
        assertThat("Invalid token should not be null", invalidToken, notNullValue());

        // 3. 잘못된 키로 생성된 토큰이므로 SignatureException 예외가 발생하는지 확인
        assertThrows(RuntimeException.class, () -> {
            jwtUtil.isValidateAccessToken(invalidToken);
        }, "A SignatureException should be thrown for a token signed with an incorrect key");
    }
}

