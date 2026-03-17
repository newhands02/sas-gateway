package com.sas.util;
import com.sas.entity.User;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
/**
 * @author 86187
 * 响应封装工具库类
 */
public class Message {



    /**
     * 构建错误响应
     */
    public static Map<String, Object> buildErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", message);
        error.put("success", false);
        return error;
    }
}
