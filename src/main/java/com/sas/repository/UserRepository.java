package com.sas.repository;

import com.sas.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    
    /**
     * 根据用户名查询用户
     */
    Mono<User> findByUsername(String username);
    
    /**
     * 根据用户名和密码查询用户
     */
    @Query("SELECT * FROM sys_user WHERE username = :username AND status = 1")
    Mono<User> findByUsernameAndActive(String username);
}
