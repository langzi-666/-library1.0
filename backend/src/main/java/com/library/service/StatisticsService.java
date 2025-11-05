package com.library.service;

import com.library.dto.StatisticsDTO;
import com.library.dto.BookStatisticsDTO;
import com.library.dto.UserStatisticsDTO;
import com.library.dto.BorrowStatisticsDTO;
import com.library.dto.PopularBookDTO;
import com.library.dto.ActiveUserDTO;

import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {
    
    /**
     * 获取综合统计数据
     */
    StatisticsDTO getStatistics();
    
    /**
     * 获取图书统计数据
     */
    BookStatisticsDTO getBookStatistics();
    
    /**
     * 获取用户统计数据
     */
    UserStatisticsDTO getUserStatistics();
    
    /**
     * 获取借阅统计数据
     */
    BorrowStatisticsDTO getBorrowStatistics();
    
    /**
     * 获取热门图书排行
     * @param limit 返回数量限制
     */
    List<PopularBookDTO> getPopularBooks(Integer limit);
    
    /**
     * 获取活跃用户排行
     * @param limit 返回数量限制
     */
    List<ActiveUserDTO> getActiveUsers(Integer limit);
}

