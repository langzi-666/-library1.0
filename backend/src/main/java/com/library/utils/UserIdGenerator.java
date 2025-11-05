package com.library.utils;

import com.library.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 用户ID生成器
 * 格式：U+年月日+4位流水号（如：U20241201001）
 */
@Component
public class UserIdGenerator {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 生成用户ID
     */
    public String generateUserId() {
        String datePrefix = "U" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 查询当天最大流水号
        String maxUserId = userMapper.selectList(null).stream()
                .filter(user -> user.getUserId().startsWith(datePrefix))
                .map(user -> user.getUserId().substring(datePrefix.length()))
                .max(String::compareTo)
                .orElse("0000");
        
        // 生成新的流水号
        int sequence = Integer.parseInt(maxUserId) + 1;
        String sequenceStr = String.format("%04d", sequence);
        
        return datePrefix + sequenceStr;
    }
}


