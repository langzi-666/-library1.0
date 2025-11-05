package com.library.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 归还图书DTO
 */
@Data
public class ReturnDTO {
    
    /**
     * 借阅记录ID或图书编号
     */
    @NotBlank(message = "借阅记录ID或图书编号不能为空")
    private String recordIdOrBookId;
    
    /**
     * 用户ID（管理员手动归还时使用，普通用户无需填写）
     */
    private String userId;
    
    /**
     * 归还时图书状态：正常/损坏/丢失
     */
    private String bookStatus = "正常";
    
    /**
     * 备注
     */
    private String remark;
}

