package com.sas.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table("sys_user")
@Data

public class User {
    @Id
    private Long id;
    
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private Integer status;
    private String avatar;
    private Integer gender;
    
    @Column("create_time")
    private LocalDateTime createTime;
    
    @Column("update_time")
    private LocalDateTime updateTime;
    
    @Column("last_login_time")
    private LocalDateTime lastLoginTime;

    private List<String> authorities = new ArrayList<>();

}
