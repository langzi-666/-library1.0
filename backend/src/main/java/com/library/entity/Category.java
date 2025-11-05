package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图书分类实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseEntity {
    
    /**
     * 分类ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Integer categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 父分类ID，0表示一级分类
     */
    private Integer parentId;
    
    /**
     * 分类级别：1/2/3
     */
    private Integer level;
    
    /**
     * 分类描述
     */
    private String description;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
}

