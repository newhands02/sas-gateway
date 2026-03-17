package com.sas.filter;

import com.sas.service.TokenBlacklistService;
import com.sas.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * @author 86187
 * 过滤转发和请求到自身的
 */
@Component
public class JwtWebFilter implements WebFilter {
    @Autowired
    private JwtUtil jwtUtils;
    
    @Autowired(required = false)
    private TokenBlacklistService blacklistService;

    // 定义白名单路径
    private static final List<String> EXCLUDE_PATHS = Arrays.asList("/auth/login", "/auth/logout", "/public/");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        // 1. 白名单直接放行
        if (EXCLUDE_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        // 2. 获取并校验 Token
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return unAuthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        // 3. 检查 token 是否在黑名单中（如果启用了黑名单服务）
        if (blacklistService != null) {
            return blacklistService.isBlacklisted(token)
                    .flatMap(isBlacklisted -> {
                        if (isBlacklisted) {
                            return unAuthorized(exchange, "Token has been revoked");
                        }
                        return checkTokenAndContinue(exchange, chain, token);
                    });
        }
        
        return checkTokenAndContinue(exchange, chain, token);
    }
    
    private Mono<Void> checkTokenAndContinue(ServerWebExchange exchange, 
                                             WebFilterChain chain, 
                                             String token) {
        // 3. 校验 Token 合法性
        if (!jwtUtils.validateToken(token)) {
            return unAuthorized(exchange, "Token is invalid or expired");
        }

        // 4. 解析用户信息并注入 Header (供本地 Controller 或下游服务使用)
        String userId = jwtUtils.getClaims(token).getSubject();

        // 使用 mutate() 修改 request
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", userId)
                .build();

        // 5. 继续过滤器链
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private Mono<Void> unAuthorized(ServerWebExchange exchange, String msg) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        // 这里可以根据需要向 Body 写入 JSON 错误信息
        return exchange.getResponse().setComplete();
    }
}
