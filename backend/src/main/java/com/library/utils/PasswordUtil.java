package com.library.utils;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加密工具类
 */
@Component
public class PasswordUtil {
    
    /**
     * MD5加密（用于兼容旧系统，实际项目中建议使用BCrypt）
     */
    public String encryptMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
    
    /**
     * 验证密码
     */
    public boolean verifyPassword(String password, String encryptedPassword) {
        String encrypted = encryptMD5(password);
        return encrypted.equals(encryptedPassword);
    }
}


