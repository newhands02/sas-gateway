package com.sas.security;

import com.sas.entity.User;
import com.sas.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.http.server.reactive.ServerHttpRequest;
import java.nio.charset.StandardCharsets;

public class JwtRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    //这段代码引发循环依赖问题
    /**
     * The dependencies of some of the beans in the application context form a cycle:
     *
     * ┌─────┐
     * |  jwtRequestFilter defined in file [D:\code\java\sas-gateway\target\classes\com\sas\security\JwtRequestFilter.class]
     * ↑     ↓
     * |  userDetailsServiceImpl defined in file [D:\code\java\sas-gateway\target\classes\com\sas\service\UserDetailsServiceImpl.class]
     * ↑     ↓
     * |  securityConfig defined in file [D:\code\java\sas-gateway\target\classes\com\sas\config\SecurityConfig.class]
     * └─────┘**/
//    public JwtRequestFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }

//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        ServerHttpResponse response = exchange.getResponse();
//        try {
//            final String requestTokenHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//            String username = null;
//            String jwtToken = null;
//
//            // JWT Token的格式为"Bearer token"
//            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
//                jwtToken = requestTokenHeader.substring(7);
//                try {
//                    username = jwtUtil.getUsernameFromToken(jwtToken);
//                } catch (IllegalArgumentException e) {
//                    System.out.println("Unable to get JWT Token");
//                } catch (ExpiredJwtException e) {
//                    System.out.println("JWT Token has expired");
//                }
//            }
//
//            // 验证token
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                User userDetails = new User();
//                if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {
//
//                }
//            }
//            // 使用 mutate 修改请求，添加新的 Header
//            ServerHttpRequest modifiedRequest = request.mutate()
//                    .header("X-User-Name", username)
//                    .build();
//            // 7. 将修改后的请求放回上下文，继续执行
//            return chain.filter(exchange.mutate().request(modifiedRequest).build());
//        }catch (Exception e) {
//            e.printStackTrace();
//            // Token 解析失败或过期
//            return unauthorized(response, "Token 无效或已过期");
//        }
//    }
//    // 辅助方法：构建未授权的响应
//    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
//        response.setStatusCode(HttpStatus.UNAUTHORIZED);
//        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//
//        String body = String.format("{\"code\": 401, \"message\": \"%s\"}", message);
//        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
//
//        return response.writeWith(Mono.just(buffer));
//    }
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//
//        chain.doFilter(request, response);
//    }
}
