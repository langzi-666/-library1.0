package com.library.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

/**
 * 借阅申请DTO
 */
@Data
public class BorrowDTO {
    
    /**
     * 图书编号或ISBN
     */
    @NotBlank(message = "图书编号或ISBN不能为空")
    private String bookIdOrIsbn;
    
    /**
     * 用户ID（管理员手动选择用户时使用，普通用户无需填写）
     */
    private String userId;
    
    /**
     * 借阅数量（默认1本）
     */
    @Min(value = 1, message = "借阅数量至少为1")
    @Max(value = 3, message = "同一图书最多借阅3本")
    private Integer quantity = 1;
    
    /**
     * 备注
     */
    private String remark;
}

