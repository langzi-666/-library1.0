package com.library.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息DTO（用于返回，不包含密码）
 */
@Data
public class UserDTO {
    
    private String userId;
    private String username;
    private String name;
    private String gender;
    private String phone;
    private String email;
    private String idCard;
    private String address;
    private String role;
    private String status;
    private LocalDateTime registerDate;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}


