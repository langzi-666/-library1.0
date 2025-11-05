package com.library.dto;

import lombok.Data;

/**
 * 状态统计数据DTO
 */
@Data
public class StatusStatisticsDTO {
    /**
     * 状态名称
     */
    private String status;
    
    /**
     * 数量
     */
    private Integer count;
}

