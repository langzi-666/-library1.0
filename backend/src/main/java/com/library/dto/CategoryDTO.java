package com.library.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 图书分类DTO
 */
@Data
public class CategoryDTO {
    
    private Integer categoryId;
    private String categoryName;
    private Integer parentId;
    private Integer level;
    private String description;
    private Integer sortOrder;
    private LocalDateTime createTime;
    
    /**
     * 子分类列表
     */
    private List<CategoryDTO> children;
}

