package com.library.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 借阅统计数据DTO
 */
@Data
public class BorrowStatisticsDTO {
    /**
     * 总借阅次数
     */
    private Integer totalBorrowCount;
    
    /**
     * 总归还次数
     */
    private Integer totalReturnCount;
    
    /**
     * 当前借阅中数量
     */
    private Integer currentBorrowingCount;
    
    /**
     * 归还率（%）
     */
    private BigDecimal returnRate;
    
    /**
     * 逾期图书数量
     */
    private Integer overdueBookCount;
    
    /**
     * 逾期用户数量
     */
    private Integer overdueUserCount;
    
    /**
     * 逾期率（%）
     */
    private BigDecimal overdueRate;
    
    /**
     * 平均逾期天数
     */
    private BigDecimal avgOverdueDays;
    
    /**
     * 借阅趋势（按时间段）
     */
    private List<TimeSeriesDTO> borrowTrend;
}

