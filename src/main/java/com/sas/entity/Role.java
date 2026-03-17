package com.sas.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("sys_role")
@Data
public class Role {
    @Id
    private Long id;
    
    @Column("role_name")
    private String roleName;
    
    @Column("role_code")
    private String roleCode;
    
    private String description;
    private Integer status;
    
    @Column("create_time")
    private LocalDateTime createTime;
    
    @Column("update_time")
    private LocalDateTime updateTime;

}
