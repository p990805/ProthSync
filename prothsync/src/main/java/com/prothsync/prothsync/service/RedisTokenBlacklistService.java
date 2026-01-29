package com.prothsync.prothsync.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTokenBlacklistService implements TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void addToBlackList(String token, Duration expirationTime) {
        if (token == null || token.isBlank()) {
            log.warn("토큰이 null이거나 비어있어 블랙리스트에 추가할 수 없습니다.");
            return;
        }

        if (expirationTime == null || expirationTime.isNegative() || expirationTime.isZero()) {
            log.warn("만료 시간이 유효하지 않아 블랙리스트에 추가할 수 없습니다.");
            return;
        }

        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "LOGGED_OUT", expirationTime);
        log.debug("토큰이 블랙리스트에 추가되었습니다. 만료 시간: {}초", expirationTime.getSeconds());
    }

    @Override
    public boolean isBlackListed(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        String key = BLACKLIST_PREFIX + token;
        Boolean hasKey = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(hasKey);
    }
}
