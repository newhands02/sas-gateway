package com.sas.service.impl;

import com.sas.entity.User;
import com.sas.repository.RoleRepository;
import com.sas.repository.UserRepository;
import com.sas.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    public UserServiceImpl(UserRepository userRepository, 
                          RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    @Override
    public Mono<User> loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    if (user.getStatus() != 1) {
                        return Mono.empty();
                    }
                    return roleRepository.findByUserId(user.getId())
                            .map(role -> role.getRoleCode())
                            .collectList()
                            .doOnNext(user::setAuthorities)
                            .thenReturn(user);
                });
    }
    
    @Override
    public Mono<Boolean> validatePassword(String username, String rawPassword) {
        return userRepository.findByUsernameAndActive(username)
                .map(user -> {
                    String storedPassword = user.getPassword();
                    if (storedPassword == null || storedPassword.isEmpty()) {
                        return false;
                    }
                    
                    if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
                        return passwordEncoder.matches(rawPassword, storedPassword);
                    } else {
                        return storedPassword.equals(rawPassword);
                    }
                })
                .defaultIfEmpty(false);
    }
    
    /**
     * 更新用户最后登录时间
     */
    public Mono<Void> updateLastLoginTime(Long userId) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    user.setLastLoginTime(LocalDateTime.now());
                    return userRepository.save(user);
                })
                .then();
    }
}
