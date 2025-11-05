package com.library.controller;

import com.library.common.Result;
import com.library.dto.*;
import com.library.service.OperationLogService;
import com.library.service.UserService;
import com.library.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户管理控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private OperationLogService operationLogService;
    
    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<UserDTO> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String userId = jwtUtil.getUserIdFromToken(token);
        UserDTO user = userService.getUserInfo(userId);
        return Result.success(user);
    }
    
    @ApiOperation("更新当前用户信息")
    @PutMapping("/info")
    public Result<UserDTO> updateCurrentUserInfo(@Validated @RequestBody UserUpdateDTO updateDTO, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String userId = jwtUtil.getUserIdFromToken(token);
            UserDTO user = userService.updateUserInfo(userId, updateDTO);
            operationLogService.saveLog(userId, "更新个人信息", "更新用户信息", getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("更新成功", user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("更新用户信息（管理员）")
    @PutMapping("/{userId}/info")
    public Result<UserDTO> updateUserInfo(@PathVariable String userId, @Validated @RequestBody UserUpdateDTO updateDTO, HttpServletRequest request) {
        try {
            UserDTO user = userService.updateUserInfo(userId, updateDTO);
            String currentUserId = (String) request.getAttribute("userId");
            if (currentUserId == null) {
                // 如果无法获取当前用户ID，使用被更新的用户ID
                currentUserId = userId;
            }
            operationLogService.saveLog(currentUserId, "更新用户信息", "管理员更新用户信息：" + userId, getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("更新成功", user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@Validated @RequestBody PasswordChangeDTO passwordDTO, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String userId = jwtUtil.getUserIdFromToken(token);
            userService.changePassword(userId, passwordDTO);
            operationLogService.saveLog(userId, "修改密码", "用户修改密码", getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("密码修改成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("查询用户列表")
    @GetMapping("/list")
    public Result<List<UserDTO>> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<UserDTO> users = userService.getUserList(keyword, role, status, pageNum, pageSize);
        return Result.success(users);
    }
    
    @ApiOperation("根据ID获取用户信息")
    @GetMapping("/{userId}")
    public Result<UserDTO> getUserById(@PathVariable String userId) {
        UserDTO user = userService.getUserInfo(userId);
        return Result.success(user);
    }
    
    @ApiOperation("删除用户")
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(@PathVariable String userId, HttpServletRequest request) {
        try {
            userService.deleteUser(userId);
            operationLogService.saveLog(userId, "删除用户", "管理员删除用户", getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("锁定/解锁用户")
    @PutMapping("/{userId}/status")
    public Result<Void> lockOrUnlockUser(@PathVariable String userId, @RequestParam String status, HttpServletRequest request) {
        try {
            userService.lockOrUnlockUser(userId, status);
            operationLogService.saveLog(userId, "锁定/解锁用户", "管理员" + ("锁定".equals(status) ? "锁定" : "解锁") + "用户", getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("重置用户密码")
    @PutMapping("/{userId}/reset-password")
    public Result<Void> resetPassword(@PathVariable String userId, @RequestParam String newPassword, HttpServletRequest request) {
        try {
            userService.resetPassword(userId, newPassword);
            operationLogService.saveLog(userId, "重置密码", "管理员重置用户密码", getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("密码重置成功", null);
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

