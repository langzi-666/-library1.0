package com.library.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书信息DTO
 */
@Data
public class BookDTO {
    
    private String bookId;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishDate;
    private String isbn;
    private Integer categoryId;
    private String categoryName;
    private BigDecimal price;
    private Integer stock;
    private Integer availableStock;
    private String description;
    private String coverImage;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

