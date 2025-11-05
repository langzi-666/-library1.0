package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.BorrowRecordDTO;
import com.library.dto.BorrowRecordQueryDTO;
import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 借阅记录Mapper接口
 */
@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
    
    /**
     * 查询借阅记录列表（包含图书和用户信息）
     */
    IPage<BorrowRecordDTO> selectBorrowRecordList(Page<BorrowRecordDTO> page, @Param("query") BorrowRecordQueryDTO query);
    
    /**
     * 查询用户的当前借阅数量
     */
    Integer countBorrowingByUserId(@Param("userId") String userId);
    
    /**
     * 查询用户是否有逾期未还的图书
     */
    Integer countOverdueByUserId(@Param("userId") String userId);
    
    /**
     * 查询用户是否已借阅该图书且未归还
     */
    Integer countBorrowingByUserIdAndBookId(@Param("userId") String userId, @Param("bookId") String bookId);
    
    /**
     * 查询图书的所有借阅记录
     */
    List<BorrowRecordDTO> selectBorrowRecordsByBookId(@Param("bookId") String bookId);
    
    /**
     * 查询所有逾期记录
     */
    IPage<BorrowRecordDTO> selectOverdueRecords(Page<BorrowRecordDTO> page, @Param("query") BorrowRecordQueryDTO query);
}

