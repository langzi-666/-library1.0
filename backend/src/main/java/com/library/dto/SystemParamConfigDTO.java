package com.library.dto;

import lombok.Data;

/**
 * 系统参数配置DTO
 */
@Data
public class SystemParamConfigDTO {
    
    /**
     * 系统名称
     */
    private String systemName;
    
    /**
     * 系统版本
     */
    private String systemVersion;
    
    /**
     * 系统公告
     */
    private String systemNotice;
    
    /**
     * 系统Logo路径
     */
    private String systemLogo;
}

