package com.sparta.jwt.application.service.util;

import com.sparta.jwt.application.dto.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j(topic = "Cache Util")
@Service
public class CacheUtil {

    // Todo: 직관성을 위해 파라미터 및 메서드 이름으로 구별했으나, 기능은 비슷하므로 코드를 합칠 수 있음.

    /**
     * Access Token 블랙리스트 처리
     *
     * @param accessToken
     * @param userInfo
     * @return
     */
    @CachePut(cacheNames = "accessTokenBlackList", key = "#accessToken")
    public UserInfoDto setAccessTokenToBlackList(String accessToken, UserInfoDto userInfo) {
        return userInfo;
    }

    /**
     * Access Token 블랙리스트 조회
     *
     * @param accessToken
     * @return
     */
    @Cacheable(cacheNames = "accessTokenBlackList", key = "#accessToken", unless = "#result == null")
    public UserInfoDto getBlackAccessToken(String accessToken) {
        // 캐시 미스 시 null 반환
        return null;
    }

    /**
     * Refresh Token 화이트리스트 처리
     *
     * @param username
     * @param userInfo
     * @return
     */
    @CachePut(cacheNames = "refreshTokenWhiteList", key = "#username")
    public UserInfoDto saveRefreshToken(String username, UserInfoDto userInfo) {
        return userInfo;
    }

    /**
     * Refresh Token 조회
     *
     * @param username
     * @return
     */
    @Cacheable(cacheNames = "refreshTokenWhiteList", key = "#username", unless = "#result == null")
    public UserInfoDto getValidRefreshToken(String username) {
        // 캐시 미스 시 null 반환
        return null;
    }

    /**
     * 화이트리스트 처리된 Refresh Token을 삭제
     */
    @CacheEvict(cacheNames = "refreshTokenWhiteList", key = "#username")
    public void deleteRefreshToken(String username) {
        // 삭제된 후 특별히 처리할 내용이 없으므로 빈 메서드로 구현
    }
}
