package com.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.OperationLog;
import com.library.mapper.OperationLogMapper;
import com.library.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 操作日志服务实现类
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLog(String userId, String operationType, String operationContent, String ipAddress, String userAgent, String result) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setOperationType(operationType);
        log.setOperationContent(operationContent);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setResult(result);
        log.setOperationTime(LocalDateTime.now());
        save(log);
    }
}


