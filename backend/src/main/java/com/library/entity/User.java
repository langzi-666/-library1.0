package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {
    
    /**
     * 用户ID（主键），格式：U+年月日+4位流水号
     */
    @TableId(type = IdType.INPUT)
    private String userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 性别：男/女/保密
     */
    private String gender;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 身份证号
     */
    private String idCard;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 角色：系统管理员/图书管理员/普通用户
     */
    private String role;
    
    /**
     * 状态：正常/锁定/已注销
     */
    private String status;
    
    /**
     * 注册日期
     */
    private LocalDateTime registerDate;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
}


