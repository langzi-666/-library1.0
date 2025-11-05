package com.library.dto;

import lombok.Data;

/**
 * 登录响应DTO
 */
@Data
public class LoginResponseDTO {
    
    private String token;
    private UserDTO userInfo;
    
    public LoginResponseDTO(String token, UserDTO userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }
}


