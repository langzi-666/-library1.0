package com.library.service;

import com.library.dto.BackupInfoDTO;
import com.library.dto.BorrowRuleConfigDTO;
import com.library.dto.SystemConfigDTO;
import com.library.dto.SystemParamConfigDTO;

import java.util.List;

/**
 * 系统设置服务接口
 */
public interface SystemConfigService {
    
    /**
     * 获取所有系统配置
     * @return 配置列表
     */
    List<SystemConfigDTO> getAllConfigs();
    
    /**
     * 根据配置键获取配置值
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);
    
    /**
     * 更新配置值
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 是否成功
     */
    boolean updateConfig(String configKey, String configValue);
    
    /**
     * 批量更新配置
     * @param configs 配置列表
     * @return 是否成功
     */
    boolean batchUpdateConfigs(List<SystemConfigDTO> configs);
    
    /**
     * 获取借阅规则配置
     * @return 借阅规则配置
     */
    BorrowRuleConfigDTO getBorrowRuleConfig();
    
    /**
     * 更新借阅规则配置
     * @param config 借阅规则配置
     * @return 是否成功
     */
    boolean updateBorrowRuleConfig(BorrowRuleConfigDTO config);
    
    /**
     * 获取系统参数配置
     * @return 系统参数配置
     */
    SystemParamConfigDTO getSystemParamConfig();
    
    /**
     * 更新系统参数配置
     * @param config 系统参数配置
     * @return 是否成功
     */
    boolean updateSystemParamConfig(SystemParamConfigDTO config);
    
    /**
     * 手动备份数据
     * @return 备份文件路径
     */
    String backupDatabase();
    
    /**
     * 获取备份历史列表
     * @return 备份信息列表
     */
    List<BackupInfoDTO> getBackupHistory();
    
    /**
     * 恢复数据库
     * @param backupFileName 备份文件名
     * @return 是否成功
     */
    boolean restoreDatabase(String backupFileName);
    
    /**
     * 删除备份文件
     * @param backupFileName 备份文件名
     * @return 是否成功
     */
    boolean deleteBackup(String backupFileName);
}

