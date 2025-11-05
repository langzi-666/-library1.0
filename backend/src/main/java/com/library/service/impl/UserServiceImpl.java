package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.dto.*;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.UserService;
import com.library.utils.CaptchaUtil;
import com.library.utils.JwtUtil;
import com.library.utils.PasswordUtil;
import com.library.utils.UserIdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CaptchaUtil captchaUtil;
    
    @Autowired
    private PasswordUtil passwordUtil;
    
    @Autowired
    private UserIdGenerator userIdGenerator;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO register(RegisterDTO registerDTO) {
        // 验证验证码（暂时禁用，但保留代码）
        // if (!captchaUtil.validateCaptcha(registerDTO.getCaptchaKey(), registerDTO.getCaptcha())) {
        //     throw new RuntimeException("验证码错误或已过期");
        // }
        
        // 验证密码一致性
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        
        // 检查用户名是否已存在
        if (existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 检查手机号是否已存在
        if (existsByPhone(registerDTO.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setUserId(userIdGenerator.generateUserId());
        user.setUsername(registerDTO.getUsername());
        // 密码不加密，直接存储
        user.setPassword(registerDTO.getPassword());
        user.setName(registerDTO.getName());
        user.setGender(registerDTO.getGender() != null ? registerDTO.getGender() : "保密");
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setIdCard(registerDTO.getIdCard());
        user.setAddress(registerDTO.getAddress());
        user.setRole("普通用户");
        user.setStatus("正常");
        user.setRegisterDate(LocalDateTime.now());
        
        userMapper.insert(user);
        
        return convertToDTO(user);
    }
    
    @Override
    public LoginResponseDTO login(LoginDTO loginDTO, String ipAddress, String userAgent) {
        // 验证验证码（暂时禁用，但保留代码）
        // if (!captchaUtil.validateCaptcha(loginDTO.getCaptchaKey(), loginDTO.getCaptcha())) {
        //     throw new RuntimeException("验证码错误或已过期");
        // }
        
        // 查询用户
        User user = getByUsernameOrEmail(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 检查账户状态
        if ("锁定".equals(user.getStatus())) {
            throw new RuntimeException("账户已被锁定，请联系管理员");
        }
        
        if ("已注销".equals(user.getStatus())) {
            throw new RuntimeException("账户已注销");
        }
        
        // 验证密码（不加密，直接比较）
        if (!loginDTO.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 生成JWT token
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
        
        // 返回登录结果
        UserDTO userDTO = convertToDTO(user);
        return new LoginResponseDTO(token, userDTO);
    }
    
    @Override
    public User getByUsernameOrEmail(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username).or().eq(User::getEmail, username);
        return userMapper.selectOne(wrapper);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return userMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public boolean existsByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return userMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public UserDTO getUserInfo(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToDTO(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO updateUserInfo(String userId, UserUpdateDTO updateDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 更新字段
        if (updateDTO.getName() != null) {
            user.setName(updateDTO.getName());
        }
        if (updateDTO.getGender() != null) {
            user.setGender(updateDTO.getGender());
        }
        if (updateDTO.getPhone() != null) {
            // 检查手机号是否被其他用户使用
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, updateDTO.getPhone()).ne(User::getUserId, userId);
            if (userMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("手机号已被其他用户使用");
            }
            user.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getEmail() != null) {
            // 检查邮箱是否被其他用户使用
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, updateDTO.getEmail()).ne(User::getUserId, userId);
            if (userMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getIdCard() != null) {
            user.setIdCard(updateDTO.getIdCard());
        }
        if (updateDTO.getAddress() != null) {
            user.setAddress(updateDTO.getAddress());
        }
        if (updateDTO.getRole() != null) {
            user.setRole(updateDTO.getRole());
        }
        if (updateDTO.getStatus() != null) {
            user.setStatus(updateDTO.getStatus());
        }
        
        userMapper.updateById(user);
        return convertToDTO(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String userId, PasswordChangeDTO passwordDTO) {
        // 验证密码一致性
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证原密码（不加密，直接比较）
        if (!passwordDTO.getOldPassword().equals(user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 检查新密码是否与原密码相同（不加密，直接比较）
        if (passwordDTO.getNewPassword().equals(user.getPassword())) {
            throw new RuntimeException("新密码不能与原密码相同");
        }
        
        // 更新密码（不加密，直接存储）
        user.setPassword(passwordDTO.getNewPassword());
        userMapper.updateById(user);
    }
    
    @Override
    public List<UserDTO> getUserList(String keyword, String role, String status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getName, keyword)
                    .or().like(User::getEmail, keyword)
                    .or().like(User::getPhone, keyword));
        }
        
        if (role != null && !role.trim().isEmpty()) {
            wrapper.eq(User::getRole, role);
        }
        
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        // 分页查询
        int offset = (pageNum - 1) * pageSize;
        wrapper.last("LIMIT " + offset + "," + pageSize);
        
        List<User> users = userMapper.selectList(wrapper);
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.deleteById(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockOrUnlockUser(String userId, String status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 密码不加密，直接存储
        user.setPassword(newPassword);
        userMapper.updateById(user);
    }
    
    /**
     * 转换为DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}


