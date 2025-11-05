package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 借阅记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("borrow_record")
public class BorrowRecord extends BaseEntity {
    
    /**
     * 记录ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Integer recordId;
    
    /**
     * 用户ID（外键）
     */
    private String userId;
    
    /**
     * 图书编号（外键）
     */
    private String bookId;
    
    /**
     * 借阅日期
     */
    private LocalDateTime borrowDate;
    
    /**
     * 应还日期
     */
    private LocalDateTime dueDate;
    
    /**
     * 归还日期
     */
    private LocalDateTime returnDate;
    
    /**
     * 续借次数
     */
    private Integer renewCount;
    
    /**
     * 状态：借阅中/已归还/已逾期
     */
    private String status;
    
    /**
     * 逾期天数
     */
    private Integer overdueDays;
    
    /**
     * 逾期费用
     */
    private BigDecimal overdueFee;
    
    /**
     * 归还时图书状态：正常/损坏/丢失
     */
    private String bookStatus;
    
    /**
     * 备注
     */
    private String remark;
}

