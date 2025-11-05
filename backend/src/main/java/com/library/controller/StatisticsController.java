package com.library.controller;

import com.library.annotation.LogOperation;
import com.library.common.Result;
import com.library.dto.*;
import com.library.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统计报表控制器
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * 获取综合统计数据
     */
    @GetMapping("/overview")
    @LogOperation(operationType = "查询", operationContent = "查询综合统计数据")
    public Result<StatisticsDTO> getStatistics() {
        StatisticsDTO statistics = statisticsService.getStatistics();
        return Result.success(statistics);
    }
    
    /**
     * 获取图书统计数据
     */
    @GetMapping("/book")
    @LogOperation(operationType = "查询", operationContent = "查询图书统计数据")
    public Result<BookStatisticsDTO> getBookStatistics() {
        BookStatisticsDTO bookStatistics = statisticsService.getBookStatistics();
        return Result.success(bookStatistics);
    }
    
    /**
     * 获取用户统计数据
     */
    @GetMapping("/user")
    @LogOperation(operationType = "查询", operationContent = "查询用户统计数据")
    public Result<UserStatisticsDTO> getUserStatistics() {
        UserStatisticsDTO userStatistics = statisticsService.getUserStatistics();
        return Result.success(userStatistics);
    }
    
    /**
     * 获取借阅统计数据
     */
    @GetMapping("/borrow")
    @LogOperation(operationType = "查询", operationContent = "查询借阅统计数据")
    public Result<BorrowStatisticsDTO> getBorrowStatistics() {
        BorrowStatisticsDTO borrowStatistics = statisticsService.getBorrowStatistics();
        return Result.success(borrowStatistics);
    }
    
    /**
     * 获取热门图书排行
     */
    @GetMapping("/popular-books")
    @LogOperation(operationType = "查询", operationContent = "查询热门图书排行")
    public Result<List<PopularBookDTO>> getPopularBooks(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<PopularBookDTO> popularBooks = statisticsService.getPopularBooks(limit);
        return Result.success(popularBooks);
    }
    
    /**
     * 获取活跃用户排行
     */
    @GetMapping("/active-users")
    @LogOperation(operationType = "查询", operationContent = "查询活跃用户排行")
    public Result<List<ActiveUserDTO>> getActiveUsers(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<ActiveUserDTO> activeUsers = statisticsService.getActiveUsers(limit);
        return Result.success(activeUsers);
    }
}

