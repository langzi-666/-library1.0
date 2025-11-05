package com.library.dto;

import lombok.Data;

/**
 * 借阅规则配置DTO
 */
@Data
public class BorrowRuleConfigDTO {
    
    /**
     * 借阅期限（天）
     */
    private Integer borrowDays;
    
    /**
     * 普通用户最大借阅数量
     */
    private Integer maxBorrowCount;
    
    /**
     * 续借次数
     */
    private Integer renewCount;
    
    /**
     * 续借期限（天）
     */
    private Integer renewDays;
    
    /**
     * 逾期费用（元/天）
     */
    private Double overdueFeePerDay;
    
    /**
     * 库存预警阈值
     */
    private Integer stockWarningThreshold;
}

