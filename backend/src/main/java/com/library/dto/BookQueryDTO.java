package com.library.dto;

import lombok.Data;

/**
 * 图书查询DTO（多条件搜索）
 */
@Data
public class BookQueryDTO {
    
    /**
     * 关键词（书名、作者、ISBN）
     */
    private String keyword;
    
    /**
     * 分类ID
     */
    private Integer categoryId;
    
    /**
     * 出版社
     */
    private String publisher;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 是否有库存（true: available_stock > 0, false: available_stock = 0）
     */
    private Boolean hasStock;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}

