package com.sas.controller;


import com.sas.entity.User;
import com.sas.service.UserService;
import com.sas.service.impl.UserServiceImpl;
import com.sas.util.JwtUtil;
import com.sas.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    // 错误消息常量
    private static final String ERR_USERNAME_EMPTY = "用户名不能为空";
    private static final String ERR_PASSWORD_EMPTY = "密码不能为空";
    private static final String ERR_CREDENTIALS_INVALID = "用户名或密码错误";
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MAX_PASSWORD_LENGTH = 100;
    @Autowired
    private JwtUtil jwtUtils;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserServiceImpl userServiceImpl;

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, Object>>> login(@RequestBody Map<String, String> credentials) {
        if (credentials == null) {
            return Mono.just(ResponseEntity.badRequest().body(Message.buildErrorResponse("请求参数不能为空")));
        }

        String username = credentials.get("username");
        String password = credentials.get("password");

        if (!validateUsername(username)) {
            return Mono.just(ResponseEntity.badRequest().body(Message.buildErrorResponse(ERR_USERNAME_EMPTY)));
        }

        if (!validatePassword(password)) {
            return Mono.just(ResponseEntity.badRequest().body(Message.buildErrorResponse(ERR_PASSWORD_EMPTY)));
        }

        return userService.validatePassword(username.trim(), password)
                .flatMap(isValid -> handlePasswordValidation(isValid, username.trim()));
    }

    /**
     * 构建登录成功响应
     */
    private Mono<ResponseEntity<Map<String, Object>>> buildLoginSuccessResponse(User user, String username) {
        String token = jwtUtils.generateToken(username);
        Map<String, Object> result = new HashMap<>();
        result.put("access_token", token);
        result.put("token_type", "Bearer");
        result.put("expires_in", JwtUtil.JWT_TOKEN_VALIDITY);
        result.put("username", username);
        result.put("nickname", user.getNickname() != null ? user.getNickname() : username);
        result.put("success", true);

        return userServiceImpl.updateLastLoginTime(user.getId())
                .thenReturn(ResponseEntity.ok(result));
    }
    /**
     * 处理密码验证结果
     */
    private Mono<ResponseEntity<Map<String, Object>>> handlePasswordValidation(Boolean isValid, String username) {
        if (!isValid) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Message.buildErrorResponse(ERR_CREDENTIALS_INVALID)));
        }

        return userService.loadUserByUsername(username)
                .flatMap(user -> buildLoginSuccessResponse(user, username))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Message.buildErrorResponse(ERR_CREDENTIALS_INVALID))));
    }
    /**
     * 验证用户名
     */
    private boolean validateUsername(String username) {
        return username != null && !username.trim().isEmpty() && username.length() <= MAX_USERNAME_LENGTH;
    }

    /**
     * 验证密码
     */
    private boolean validatePassword(String password) {
        return password != null && !password.trim().isEmpty() && password.length() <= MAX_PASSWORD_LENGTH;
    }
    @PostMapping("/logout")
    public Mono<ResponseEntity<Map<String, Object>>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtUtils.validateToken(token)) {
                    result.put("message", "登出成功");
                    result.put("success", true);
                    return Mono.just(ResponseEntity.ok(result));
                } else {
                    result.put("message", "Token 无效或已过期");
                    result.put("success", false);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result));
                }
            } catch (Exception e) {
                result.put("message", "登出失败：" + e.getMessage());
                result.put("success", false);
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result));
            }
        } else {
            result.put("message", "已登出");
            result.put("success", true);
            return Mono.just(ResponseEntity.ok(result));
        }
    }

}
