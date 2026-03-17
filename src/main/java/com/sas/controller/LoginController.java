package com.sas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class LoginController {

    @GetMapping("/test")
    public Mono<ResponseEntity<Map<String, Object>>> test(){
        Map<String, Object> result = new HashMap<>();
        result.put("test", "2222");
        return Mono.just(ResponseEntity.ok(result));
    }

}
