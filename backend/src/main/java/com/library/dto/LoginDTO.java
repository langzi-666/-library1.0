package com.library.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录DTO
 */
@Data
public class LoginDTO {
    
    @NotBlank(message = "用户名或邮箱不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    // 验证码字段（暂时禁用，但保留代码）
    // @NotBlank(message = "验证码不能为空")
    private String captcha;
    
    // @NotBlank(message = "验证码Key不能为空")
    private String captchaKey;
    
    private Boolean rememberMe;
}


