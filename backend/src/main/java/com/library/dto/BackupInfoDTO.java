package com.library.dto;

import lombok.Data;

/**
 * 备份信息DTO
 */
@Data
public class BackupInfoDTO {
    
    /**
     * 备份文件名
     */
    private String fileName;
    
    /**
     * 备份文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 备份时间
     */
    private String backupTime;
    
    /**
     * 备份类型（手动/自动）
     */
    private String backupType;
    
    /**
     * 备份描述
     */
    private String description;
}

