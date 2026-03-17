package com.sas.service;

import com.sas.entity.User;
import reactor.core.publisher.Mono;

public interface UserService {
    public Mono<User> loadUserByUsername(String username);
    public Mono<Boolean> validatePassword(String username, String rawPassword);
}
