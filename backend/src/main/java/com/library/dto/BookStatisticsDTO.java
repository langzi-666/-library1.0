package com.library.dto;

import lombok.Data;
import java.util.List;

/**
 * 图书统计数据DTO
 */
@Data
public class BookStatisticsDTO {
    /**
     * 图书总数
     */
    private Integer totalBooks;
    
    /**
     * 总库存数量
     */
    private Integer totalStock;
    
    /**
     * 在库数量
     */
    private Integer availableStock;
    
    /**
     * 已借出数量
     */
    private Integer borrowedStock;
    
    /**
     * 库存不足数量（库存<5本）
     */
    private Integer lowStockCount;
    
    /**
     * 分类统计
     */
    private List<CategoryStatisticsDTO> categoryStatistics;
    
    /**
     * 图书状态分布
     */
    private List<StatusStatisticsDTO> statusStatistics;
    
    /**
     * 新增图书统计（按时间段）
     */
    private List<TimeSeriesDTO> newBooksTrend;
}

