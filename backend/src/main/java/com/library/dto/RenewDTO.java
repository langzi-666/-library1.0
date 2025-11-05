package com.library.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 续借DTO
 */
@Data
public class RenewDTO {
    
    /**
     * 借阅记录ID
     */
    @NotNull(message = "借阅记录ID不能为空")
    private Integer recordId;
    
    /**
     * 续借天数（默认30天）
     */
    private Integer days = 30;
}

