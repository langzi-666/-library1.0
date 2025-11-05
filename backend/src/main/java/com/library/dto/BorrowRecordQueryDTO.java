package com.library.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 借阅记录查询DTO
 */
@Data
public class BorrowRecordQueryDTO {
    
    /**
     * 用户ID（查询特定用户的借阅记录）
     */
    private String userId;
    
    /**
     * 图书编号或ISBN
     */
    private String bookIdOrIsbn;
    
    /**
     * 图书名称（模糊搜索）
     */
    private String bookTitle;
    
    /**
     * 状态：借阅中/已归还/已逾期
     */
    private String status;
    
    /**
     * 是否逾期（true: 只查询逾期记录）
     */
    private Boolean overdue;
    
    /**
     * 借阅日期起始
     */
    private LocalDateTime borrowDateStart;
    
    /**
     * 借阅日期结束
     */
    private LocalDateTime borrowDateEnd;
    
    /**
     * 页码（默认1）
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量（默认10）
     */
    private Integer pageSize = 10;
}

