package com.sas.repository;

import com.sas.entity.Role;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RoleRepository extends ReactiveCrudRepository<Role, Long> {
    
    /**
     * 根据用户 ID 查询角色列表
     */
    @Query("SELECT r.* FROM sys_role r " +
           "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
           "WHERE ur.user_id = :userId AND r.status = 1")
    Flux<Role> findByUserId(Long userId);
}
