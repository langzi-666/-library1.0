package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.OperationLog;

/**
 * 操作日志服务接口
 */
public interface OperationLogService extends IService<OperationLog> {
    
    /**
     * 记录操作日志
     */
    void saveLog(String userId, String operationType, String operationContent, String ipAddress, String userAgent, String result);
}


