package com.library.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 活跃用户DTO
 */
@Data
public class ActiveUserDTO {
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 借阅次数
     */
    private Integer borrowCount;
    
    /**
     * 累计借阅时长（天）
     */
    private BigDecimal totalBorrowDays;
}

