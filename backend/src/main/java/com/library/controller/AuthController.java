package com.library.controller;

import com.library.common.Result;
import com.library.dto.LoginDTO;
import com.library.dto.LoginResponseDTO;
import com.library.dto.RegisterDTO;
import com.library.dto.UserDTO;
import com.library.service.OperationLogService;
import com.library.service.UserService;
import com.library.utils.CaptchaUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 认证控制器（登录、注册）
 */
@Api(tags = "认证管理")
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CaptchaUtil captchaUtil;
    
    @Autowired
    private OperationLogService operationLogService;
    
    @ApiOperation("生成验证码")
    @GetMapping("/captcha")
    public Result<Map<String, String>> generateCaptcha() {
        Map<String, String> captcha = captchaUtil.generateCaptcha();
        return Result.success(captcha);
    }
    
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<UserDTO> register(@Validated @RequestBody RegisterDTO registerDTO) {
        try {
            UserDTO user = userService.register(registerDTO);
            operationLogService.saveLog(user.getUserId(), "注册", "用户注册成功", null, null, "成功");
            return Result.success("注册成功", user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@Validated @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        try {
            String ipAddress = getIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            LoginResponseDTO response = userService.login(loginDTO, ipAddress, userAgent);
            operationLogService.saveLog(response.getUserInfo().getUserId(), "登录", "用户登录成功", ipAddress, userAgent, "成功");
            
            return Result.success("登录成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

