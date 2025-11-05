package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.dto.BorrowDTO;
import com.library.dto.BorrowRecordDTO;
import com.library.dto.BorrowRecordQueryDTO;
import com.library.dto.RenewDTO;
import com.library.dto.ReturnDTO;
import com.library.entity.BorrowRecord;

import java.util.List;

/**
 * 借阅服务接口
 */
public interface BorrowService extends IService<BorrowRecord> {
    
    /**
     * 借阅图书
     */
    BorrowRecordDTO borrowBook(BorrowDTO borrowDTO, String currentUserId);
    
    /**
     * 归还图书
     */
    BorrowRecordDTO returnBook(ReturnDTO returnDTO, String currentUserId);
    
    /**
     * 续借图书
     */
    BorrowRecordDTO renewBook(RenewDTO renewDTO, String currentUserId);
    
    /**
     * 查询借阅记录列表（分页）
     */
    IPage<BorrowRecordDTO> queryBorrowRecords(BorrowRecordQueryDTO queryDTO);
    
    /**
     * 查询用户的借阅记录
     */
    IPage<BorrowRecordDTO> queryUserBorrowRecords(String userId, BorrowRecordQueryDTO queryDTO);
    
    /**
     * 查询图书的借阅记录
     */
    List<BorrowRecordDTO> queryBookBorrowRecords(String bookId);
    
    /**
     * 查询逾期记录
     */
    IPage<BorrowRecordDTO> queryOverdueRecords(BorrowRecordQueryDTO queryDTO);
    
    /**
     * 根据ID获取借阅记录
     */
    BorrowRecordDTO getBorrowRecordById(Integer recordId);
    
    /**
     * 检查用户是否可以借阅
     */
    void checkBorrowPermission(String userId);
    
    /**
     * 自动更新逾期状态
     */
    void updateOverdueStatus();
}

