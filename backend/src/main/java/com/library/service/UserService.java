package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.dto.*;
import com.library.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    UserDTO register(RegisterDTO registerDTO);
    
    /**
     * 用户登录
     */
    LoginResponseDTO login(LoginDTO loginDTO, String ipAddress, String userAgent);
    
    /**
     * 根据用户名或邮箱查询用户
     */
    User getByUsernameOrEmail(String username);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);
    
    /**
     * 获取用户信息（不包含密码）
     */
    UserDTO getUserInfo(String userId);
    
    /**
     * 更新用户信息
     */
    UserDTO updateUserInfo(String userId, UserUpdateDTO updateDTO);
    
    /**
     * 修改密码
     */
    void changePassword(String userId, PasswordChangeDTO passwordDTO);
    
    /**
     * 查询用户列表（分页）
     */
    List<UserDTO> getUserList(String keyword, String role, String status, Integer pageNum, Integer pageSize);
    
    /**
     * 删除用户
     */
    void deleteUser(String userId);
    
    /**
     * 锁定/解锁用户
     */
    void lockOrUnlockUser(String userId, String status);
    
    /**
     * 重置用户密码
     */
    void resetPassword(String userId, String newPassword);
}


