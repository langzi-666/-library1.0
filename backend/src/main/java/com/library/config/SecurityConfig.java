package com.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;

/**
 * 安全配置类
 * 用于增强系统安全性
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer {
    
    /**
     * 字符编码过滤器
     * 防止字符编码相关安全问题
     */
    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }
    
    /**
     * 安全响应头配置
     * 通过拦截器添加安全响应头
     */
    public static void addSecurityHeaders(HttpServletResponse response) {
        // 防止XSS攻击
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        // 防止内容类型嗅探
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        // 禁用缓存（对于敏感接口）
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }
}

