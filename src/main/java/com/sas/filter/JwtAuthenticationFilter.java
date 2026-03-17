//package com.sas.filter;
//
//import com.sas.util.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * @author 86187
// * Global filter只能在转发到其他服务的情况下过滤请求
// */
//@Component
//public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
//    @Autowired
//    private JwtUtil jwtUtils;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//
//        // 1. 白名单放行 (如：登录接口)
//        String path = request.getURI().getPath();
//        if (path.contains("/auth/login")) {
//            return chain.filter(exchange);
//        }
//        // 2. 获取 Authorization 里的 Token
//        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        String token = authHeader.substring(7);
//        // 3. 校验 Token
//        if (!jwtUtils.validateToken(token)) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        // 4. 解析用户信息并传递给下游微服务
//        String userId = jwtUtils.getClaims(token).getSubject();
//        ServerHttpRequest mutableRequest = exchange.getRequest().mutate()
//                .header("X-User-Id", userId)
//                .build();
//        return chain.filter(exchange.mutate().request(mutableRequest).build());
//    }
//
//    @Override
//    public int getOrder() {
//        return -100;
//    }
//}
