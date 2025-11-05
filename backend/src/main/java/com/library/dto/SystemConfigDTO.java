package com.library.dto;

import lombok.Data;

/**
 * 系统设置DTO
 */
@Data
public class SystemConfigDTO {
    
    /**
     * 配置ID
     */
    private Integer configId;
    
    /**
     * 配置键
     */
    private String configKey;
    
    /**
     * 配置值
     */
    private String configValue;
    
    /**
     * 配置描述
     */
    private String configDesc;
    
    /**
     * 更新时间
     */
    private String updateTime;
}

