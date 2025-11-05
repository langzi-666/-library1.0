package com.library.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 创建图书DTO
 */
@Data
public class BookCreateDTO {
    
    @NotBlank(message = "书名不能为空")
    @Size(max = 200, message = "书名长度不能超过200个字符")
    private String title;
    
    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者长度不能超过100个字符")
    private String author;
    
    @NotBlank(message = "出版社不能为空")
    @Size(max = 100, message = "出版社长度不能超过100个字符")
    private String publisher;
    
    @NotNull(message = "出版日期不能为空")
    private LocalDate publishDate;
    
    @NotBlank(message = "ISBN不能为空")
    @Size(max = 20, message = "ISBN长度不能超过20个字符")
    private String isbn;
    
    @NotNull(message = "分类ID不能为空")
    private Integer categoryId;
    
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.0", message = "价格不能为负数")
    private BigDecimal price;
    
    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能为负数")
    private Integer stock;
    
    @Size(max = 5000, message = "描述长度不能超过5000个字符")
    private String description;
    
    @Size(max = 200, message = "封面图片路径长度不能超过200个字符")
    private String coverImage;
}

