package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 图书实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("book")
public class Book extends BaseEntity {
    
    /**
     * 图书编号（主键），格式：BK+年月日+4位流水号
     */
    @TableId(type = IdType.INPUT)
    private String bookId;
    
    /**
     * 书名
     */
    private String title;
    
    /**
     * 作者，多个作者用逗号分隔
     */
    private String author;
    
    /**
     * 出版社
     */
    private String publisher;
    
    /**
     * 出版日期
     */
    private LocalDate publishDate;
    
    /**
     * ISBN号
     */
    private String isbn;
    
    /**
     * 分类ID（外键）
     */
    private Integer categoryId;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 库存数量
     */
    private Integer stock;
    
    /**
     * 可用库存（库存-借出数量）
     */
    private Integer availableStock;
    
    /**
     * 图书描述
     */
    private String description;
    
    /**
     * 封面图片路径
     */
    private String coverImage;
    
    /**
     * 状态：在库/已借出/已下架/损坏
     */
    private String status;
}

