package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Data
@TableName("operation_log")
public class OperationLog {
    
    /**
     * 日志ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Integer logId;
    
    /**
     * 操作用户ID
     */
    private String userId;
    
    /**
     * 操作类型：登录/登出/借阅/归还/添加图书等
     */
    private String operationType;
    
    /**
     * 操作内容
     */
    private String operationContent;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 用户代理
     */
    private String userAgent;
    
    /**
     * 操作时间
     */
    private LocalDateTime operationTime;
    
    /**
     * 操作结果：成功/失败
     */
    private String result;
}


