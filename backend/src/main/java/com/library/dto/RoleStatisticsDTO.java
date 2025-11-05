package com.library.dto;

import lombok.Data;

/**
 * 角色统计数据DTO
 */
@Data
public class RoleStatisticsDTO {
    /**
     * 角色名称
     */
    private String role;
    
    /**
     * 用户数量
     */
    private Integer userCount;
}

