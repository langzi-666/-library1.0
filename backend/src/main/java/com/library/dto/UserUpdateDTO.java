package com.library.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户更新DTO
 */
@Data
public class UserUpdateDTO {
    
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;
    
    private String gender;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$", message = "身份证号格式不正确")
    private String idCard;
    
    @Size(max = 200, message = "地址长度不能超过200个字符")
    private String address;
    
    private String role;
    
    private String status;
}


