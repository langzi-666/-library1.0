package com.library.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 统计数据DTO
 */
@Data
public class StatisticsDTO {
    /**
     * 图书统计
     */
    private BookStatisticsDTO bookStatistics;
    
    /**
     * 用户统计
     */
    private UserStatisticsDTO userStatistics;
    
    /**
     * 借阅统计
     */
    private BorrowStatisticsDTO borrowStatistics;
    
    /**
     * 热门图书排行（TOP 10）
     */
    private List<PopularBookDTO> popularBooks;
    
    /**
     * 活跃用户排行（TOP 10）
     */
    private List<ActiveUserDTO> activeUsers;
}

