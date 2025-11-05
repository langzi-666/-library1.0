package com.library.interceptor;

import com.library.common.Result;
import com.library.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 允许OPTIONS请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        String token = request.getHeader("Authorization");
        
        // 如果token为空或格式不正确
        if (token == null || !token.startsWith("Bearer ")) {
            return sendError(response, "未登录或token无效");
        }
        
        token = token.substring(7);
        
        // 验证token
        if (!jwtUtil.validateToken(token)) {
            return sendError(response, "token已过期或无效");
        }
        
        // 将用户信息存储到request中
        String userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("role", role);
        
        return true;
    }
    
    /**
     * 发送错误响应
     */
    private boolean sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        Result<Object> result = Result.error(message);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), result);
        
        return false;
    }
}


