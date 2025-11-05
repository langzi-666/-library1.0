package com.library.dto;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * 更新分类DTO
 */
@Data
public class CategoryUpdateDTO {
    
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    private String categoryName;
    
    @Min(value = 0, message = "父分类ID不能为负数")
    private Integer parentId;
    
    @Min(value = 1, message = "分类级别最小为1")
    @Max(value = 3, message = "分类级别最大为3")
    private Integer level;
    
    @Size(max = 200, message = "分类描述长度不能超过200个字符")
    private String description;
    
    @Min(value = 0, message = "排序顺序不能为负数")
    private Integer sortOrder;
}

