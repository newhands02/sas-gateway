package com.sas.service;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Token 黑名单服务（可选）
 * 用于实现 JWT token 的主动失效
 */
@Service
public class TokenBlacklistService {
    
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    
    public TokenBlacklistService(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    /**
     * 将 token 加入黑名单
     * @param token JWT token
     * @param expirationTime token 剩余有效期（秒）
     * @return 操作结果
     */
    public Mono<Boolean> blacklistToken(String token, long expirationTime) {
        String key = BLACKLIST_PREFIX + token;
        return redisTemplate.opsForValue()
                .set(key, "blacklisted", Duration.ofSeconds(expirationTime));
    }
    
    /**
     * 检查 token 是否在黑名单中
     * @param token JWT token
     * @return 是否在黑名单中
     */
    public Mono<Boolean> isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return redisTemplate.hasKey(key);
    }
}
