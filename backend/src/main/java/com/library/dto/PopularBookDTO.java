package com.library.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 热门图书DTO
 */
@Data
public class PopularBookDTO {
    /**
     * 图书编号
     */
    private String bookId;
    
    /**
     * 书名
     */
    private String title;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 借阅次数
     */
    private Integer borrowCount;
    
    /**
     * 平均借阅时长（天）
     */
    private BigDecimal avgBorrowDays;
    
    /**
     * 借阅率（借阅次数/库存总量）
     */
    private BigDecimal borrowRate;
}

