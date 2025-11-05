package com.library.dto;

import lombok.Data;

/**
 * 分类统计数据DTO
 */
@Data
public class CategoryStatisticsDTO {
    /**
     * 分类ID
     */
    private Integer categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 图书数量
     */
    private Integer bookCount;
    
    /**
     * 库存数量
     */
    private Integer stockCount;
}

