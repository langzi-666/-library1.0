package com.library.dto;

import lombok.Data;

/**
 * 时间序列数据DTO
 */
@Data
public class TimeSeriesDTO {
    /**
     * 时间标签（如：2024-01、2024-01-01等）
     */
    private String timeLabel;
    
    /**
     * 数值
     */
    private Integer value;
}

