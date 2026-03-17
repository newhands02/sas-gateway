package com.sas.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BCrypt 密码加密工具类
 */
@Component
public class PasswordEncoderUtil {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    public PasswordEncoderUtil() {
        // 默认强度为 10，范围是 4-31，值越大加密强度越高，耗时越长
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    /**
     * 指定加密强度
     * @param strength 加密强度（4-31），推荐 10
     */
    public PasswordEncoderUtil(int strength) {
        this.passwordEncoder = new BCryptPasswordEncoder(strength);
    }
    
    /**
     * 对明文密码进行加密
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return passwordEncoder.encode(rawPassword);
    }
    
    /**
     * 验证明文密码与加密密码是否匹配
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 获取 BCryptPasswordEncoder 实例
     * @return BCryptPasswordEncoder
     */
    public BCryptPasswordEncoder getEncoder() {
        return passwordEncoder;
    }
}
