package com.library.utils;

import com.library.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 图书ID生成器
 * 格式：BK+年月日+4位流水号（如：BK20241201001）
 */
@Component
public class BookIdGenerator {
    
    @Autowired
    private BookMapper bookMapper;
    
    /**
     * 生成图书ID
     */
    public String generateBookId() {
        String datePrefix = "BK" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 查询当天最大流水号
        String maxBookId = bookMapper.selectList(null).stream()
                .filter(book -> book.getBookId() != null && book.getBookId().startsWith(datePrefix))
                .map(book -> book.getBookId().substring(datePrefix.length()))
                .max(String::compareTo)
                .orElse("0000");
        
        // 生成新的流水号
        int sequence = Integer.parseInt(maxBookId) + 1;
        String sequenceStr = String.format("%04d", sequence);
        
        return datePrefix + sequenceStr;
    }
}

