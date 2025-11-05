package com.library.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 借阅记录DTO（包含图书和用户信息）
 */
@Data
public class BorrowRecordDTO {
    
    private Integer recordId;
    private String userId;
    private String username;
    private String userName; // 用户姓名
    private String bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private Integer renewCount;
    private String status;
    private Integer overdueDays;
    private BigDecimal overdueFee;
    private String bookStatus;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

