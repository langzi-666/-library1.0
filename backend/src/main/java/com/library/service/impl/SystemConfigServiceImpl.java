package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.dto.BackupInfoDTO;
import com.library.dto.BorrowRuleConfigDTO;
import com.library.dto.SystemConfigDTO;
import com.library.dto.SystemParamConfigDTO;
import com.library.entity.SystemConfig;
import com.library.mapper.SystemConfigMapper;
import com.library.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统设置服务实现类
 */
@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    
    @Value("${spring.datasource.username}")
    private String datasourceUsername;
    
    @Value("${spring.datasource.password}")
    private String datasourcePassword;
    
    @Value("${backup.path:./backup}")
    private String backupPath;
    
    @Override
    public List<SystemConfigDTO> getAllConfigs() {
        List<SystemConfig> configs = systemConfigMapper.selectList(null);
        return configs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    public String getConfigValue(String configKey) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        SystemConfig config = systemConfigMapper.selectOne(wrapper);
        return config != null ? config.getConfigValue() : null;
    }
    
    @Override
    @Transactional
    public boolean updateConfig(String configKey, String configValue) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        SystemConfig config = systemConfigMapper.selectOne(wrapper);
        
        if (config != null) {
            config.setConfigValue(configValue);
            config.setUpdateTime(LocalDateTime.now());
            return systemConfigMapper.updateById(config) > 0;
        } else {
            // 如果配置不存在，创建新配置
            config = new SystemConfig();
            config.setConfigKey(configKey);
            config.setConfigValue(configValue);
            config.setUpdateTime(LocalDateTime.now());
            return systemConfigMapper.insert(config) > 0;
        }
    }
    
    @Override
    @Transactional
    public boolean batchUpdateConfigs(List<SystemConfigDTO> configs) {
        for (SystemConfigDTO dto : configs) {
            updateConfig(dto.getConfigKey(), dto.getConfigValue());
        }
        return true;
    }
    
    @Override
    public BorrowRuleConfigDTO getBorrowRuleConfig() {
        BorrowRuleConfigDTO config = new BorrowRuleConfigDTO();
        String borrowDays = getConfigValue("borrow_days");
        String maxBorrowCount = getConfigValue("max_borrow_count");
        String renewCount = getConfigValue("renew_count");
        String renewDays = getConfigValue("renew_days");
        String overdueFeePerDay = getConfigValue("overdue_fee_per_day");
        String stockWarningThreshold = getConfigValue("stock_warning_threshold");
        
        config.setBorrowDays(borrowDays != null ? Integer.parseInt(borrowDays) : 30);
        config.setMaxBorrowCount(maxBorrowCount != null ? Integer.parseInt(maxBorrowCount) : 5);
        config.setRenewCount(renewCount != null ? Integer.parseInt(renewCount) : 1);
        config.setRenewDays(renewDays != null ? Integer.parseInt(renewDays) : 30);
        config.setOverdueFeePerDay(overdueFeePerDay != null ? Double.parseDouble(overdueFeePerDay) : 1.00);
        config.setStockWarningThreshold(stockWarningThreshold != null ? Integer.parseInt(stockWarningThreshold) : 5);
        return config;
    }
    
    @Override
    @Transactional
    public boolean updateBorrowRuleConfig(BorrowRuleConfigDTO config) {
        updateConfig("borrow_days", String.valueOf(config.getBorrowDays()));
        updateConfig("max_borrow_count", String.valueOf(config.getMaxBorrowCount()));
        updateConfig("renew_count", String.valueOf(config.getRenewCount()));
        updateConfig("renew_days", String.valueOf(config.getRenewDays()));
        updateConfig("overdue_fee_per_day", String.valueOf(config.getOverdueFeePerDay()));
        updateConfig("stock_warning_threshold", String.valueOf(config.getStockWarningThreshold()));
        return true;
    }
    
    @Override
    public SystemParamConfigDTO getSystemParamConfig() {
        SystemParamConfigDTO config = new SystemParamConfigDTO();
        config.setSystemName(getConfigValue("system_name"));
        config.setSystemVersion(getConfigValue("system_version"));
        String notice = getConfigValue("system_notice");
        config.setSystemNotice(notice != null ? notice : "");
        String logo = getConfigValue("system_logo");
        config.setSystemLogo(logo != null ? logo : "");
        return config;
    }
    
    @Override
    @Transactional
    public boolean updateSystemParamConfig(SystemParamConfigDTO config) {
        if (config.getSystemName() != null) {
            updateConfig("system_name", config.getSystemName());
        }
        if (config.getSystemVersion() != null) {
            updateConfig("system_version", config.getSystemVersion());
        }
        if (config.getSystemNotice() != null) {
            updateConfig("system_notice", config.getSystemNotice());
        }
        if (config.getSystemLogo() != null) {
            updateConfig("system_logo", config.getSystemLogo());
        }
        return true;
    }
    
    @Override
    public String backupDatabase() {
        try {
            // 创建备份目录
            Path backupDir = Paths.get(backupPath);
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }
            
            // 生成备份文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupFileName = "library_backup_manual_" + timestamp + ".sql";
            Path backupFile = backupDir.resolve(backupFileName);
            
            // 提取数据库名称
            String dbName = extractDatabaseName(datasourceUrl);
            
            // 执行mysqldump命令（需要系统环境中有mysqldump）
            // Windows系统可能需要完整路径，如：C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe
            ProcessBuilder pb = new ProcessBuilder(
                "mysqldump",
                "-u", datasourceUsername,
                "-p" + datasourcePassword,
                "--single-transaction",
                "--routines",
                "--triggers",
                dbName
            );
            pb.redirectOutput(backupFile.toFile());
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                return backupFileName;
            } else {
                // 如果mysqldump不可用，返回提示信息
                throw new RuntimeException("备份失败：mysqldump命令不可用或路径未配置。退出码: " + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("备份数据库失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<BackupInfoDTO> getBackupHistory() {
        List<BackupInfoDTO> backupList = new ArrayList<>();
        try {
            Path backupDir = Paths.get(backupPath);
            if (!Files.exists(backupDir)) {
                return backupList;
            }
            
            Files.list(backupDir)
                .filter(path -> path.toString().endsWith(".sql"))
                .sorted((p1, p2) -> {
                    try {
                        return Files.getLastModifiedTime(p2).compareTo(Files.getLastModifiedTime(p1));
                    } catch (IOException e) {
                        return 0;
                    }
                })
                .forEach(path -> {
                    BackupInfoDTO backup = new BackupInfoDTO();
                    backup.setFileName(path.getFileName().toString());
                    try {
                        backup.setFileSize(Files.size(path));
                        backup.setBackupTime(Files.getLastModifiedTime(path)
                            .toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    } catch (IOException e) {
                        backup.setFileSize(0L);
                        backup.setBackupTime("");
                    }
                    // 判断备份类型（根据文件名包含manual或auto）
                    if (path.getFileName().toString().contains("manual")) {
                        backup.setBackupType("手动");
                    } else {
                        backup.setBackupType("自动");
                    }
                    backupList.add(backup);
                });
        } catch (IOException e) {
            throw new RuntimeException("获取备份历史失败: " + e.getMessage(), e);
        }
        return backupList;
    }
    
    @Override
    @Transactional
    public boolean restoreDatabase(String backupFileName) {
        try {
            Path backupFile = Paths.get(backupPath, backupFileName);
            if (!Files.exists(backupFile)) {
                throw new RuntimeException("备份文件不存在: " + backupFileName);
            }
            
            // 提取数据库名称
            String dbName = extractDatabaseName(datasourceUrl);
            
            // 执行mysql命令恢复数据库（需要系统环境中有mysql命令）
            // Windows系统可能需要完整路径，如：C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
            ProcessBuilder pb = new ProcessBuilder(
                "mysql",
                "-u", datasourceUsername,
                "-p" + datasourcePassword,
                dbName
            );
            pb.redirectInput(backupFile.toFile());
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                return true;
            } else {
                throw new RuntimeException("恢复失败：mysql命令不可用或路径未配置。退出码: " + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("恢复数据库失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteBackup(String backupFileName) {
        try {
            Path backupFile = Paths.get(backupPath, backupFileName);
            if (Files.exists(backupFile)) {
                Files.delete(backupFile);
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException("删除备份文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将实体转换为DTO
     */
    private SystemConfigDTO convertToDTO(SystemConfig config) {
        SystemConfigDTO dto = new SystemConfigDTO();
        dto.setConfigId(config.getConfigId());
        dto.setConfigKey(config.getConfigKey());
        dto.setConfigValue(config.getConfigValue());
        dto.setConfigDesc(config.getConfigDesc());
        if (config.getUpdateTime() != null) {
            dto.setUpdateTime(config.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return dto;
    }
    
    /**
     * 从JDBC URL中提取数据库名称
     */
    private String extractDatabaseName(String url) {
        // 格式: jdbc:mysql://localhost:3306/library?...
        int start = url.lastIndexOf("/") + 1;
        int end = url.indexOf("?");
        if (end == -1) {
            end = url.length();
        }
        return url.substring(start, end);
    }
}

