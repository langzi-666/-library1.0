package com.library.dto;

import lombok.Data;
import java.util.List;

/**
 * 用户统计数据DTO
 */
@Data
public class UserStatisticsDTO {
    /**
     * 用户总数
     */
    private Integer totalUsers;
    
    /**
     * 角色分布
     */
    private List<RoleStatisticsDTO> roleStatistics;
    
    /**
     * 用户状态分布
     */
    private List<StatusStatisticsDTO> statusStatistics;
    
    /**
     * 新增用户统计（按时间段）
     */
    private List<TimeSeriesDTO> newUsersTrend;
    
    /**
     * 活跃用户数量（最近30天有借阅行为的用户）
     */
    private Integer activeUsers;
}

