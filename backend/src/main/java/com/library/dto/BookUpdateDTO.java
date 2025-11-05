package com.library.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 更新图书DTO
 */
@Data
public class BookUpdateDTO {
    
    @Size(max = 200, message = "书名长度不能超过200个字符")
    private String title;
    
    @Size(max = 100, message = "作者长度不能超过100个字符")
    private String author;
    
    @Size(max = 100, message = "出版社长度不能超过100个字符")
    private String publisher;
    
    private LocalDate publishDate;
    
    @Size(max = 20, message = "ISBN长度不能超过20个字符")
    private String isbn;
    
    private Integer categoryId;
    
    @DecimalMin(value = "0.0", message = "价格不能为负数")
    private BigDecimal price;
    
    @Min(value = 0, message = "库存数量不能为负数")
    private Integer stock;
    
    @Size(max = 5000, message = "描述长度不能超过5000个字符")
    private String description;
    
    @Size(max = 200, message = "封面图片路径长度不能超过200个字符")
    private String coverImage;
    
    @Pattern(regexp = "在库|已借出|已下架|损坏", message = "状态必须是：在库、已借出、已下架、损坏之一")
    private String status;
}

