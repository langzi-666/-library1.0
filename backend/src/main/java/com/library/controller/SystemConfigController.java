package com.library.controller;

import com.library.annotation.LogOperation;
import com.library.common.Result;
import com.library.dto.BackupInfoDTO;
import com.library.dto.BorrowRuleConfigDTO;
import com.library.dto.SystemConfigDTO;
import com.library.dto.SystemParamConfigDTO;
import com.library.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统设置控制器
 */
@Api(tags = "系统设置管理")
@RestController
@RequestMapping("/system")
public class SystemConfigController {
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @ApiOperation("获取所有系统配置")
    @GetMapping("/config/list")
    @LogOperation(operationType = "查询系统配置", operationContent = "查询所有系统配置")
    public Result<List<SystemConfigDTO>> getAllConfigs() {
        try {
            List<SystemConfigDTO> configs = systemConfigService.getAllConfigs();
            return Result.success(configs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("根据配置键获取配置值")
    @GetMapping("/config/{configKey}")
    @LogOperation(operationType = "查询系统配置", operationContent = "根据配置键查询配置值")
    public Result<String> getConfigValue(@PathVariable String configKey) {
        try {
            String value = systemConfigService.getConfigValue(configKey);
            return Result.success(value);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("更新配置值")
    @PutMapping("/config/{configKey}")
    @LogOperation(operationType = "修改系统配置", operationContent = "更新系统配置值")
    public Result<Void> updateConfig(@PathVariable String configKey, @RequestBody SystemConfigDTO configDTO) {
        try {
            boolean success = systemConfigService.updateConfig(configKey, configDTO.getConfigValue());
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("更新配置失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("批量更新配置")
    @PutMapping("/config/batch")
    @LogOperation(operationType = "修改系统配置", operationContent = "批量更新系统配置")
    public Result<Void> batchUpdateConfigs(@RequestBody List<SystemConfigDTO> configs) {
        try {
            boolean success = systemConfigService.batchUpdateConfigs(configs);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("批量更新配置失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("获取借阅规则配置")
    @GetMapping("/borrow-rule")
    @LogOperation(operationType = "查询系统配置", operationContent = "查询借阅规则配置")
    public Result<BorrowRuleConfigDTO> getBorrowRuleConfig() {
        try {
            BorrowRuleConfigDTO config = systemConfigService.getBorrowRuleConfig();
            return Result.success(config);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("更新借阅规则配置")
    @PutMapping("/borrow-rule")
    @LogOperation(operationType = "修改系统配置", operationContent = "更新借阅规则配置")
    public Result<Void> updateBorrowRuleConfig(@RequestBody BorrowRuleConfigDTO config) {
        try {
            boolean success = systemConfigService.updateBorrowRuleConfig(config);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("更新借阅规则配置失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("获取系统参数配置")
    @GetMapping("/param")
    @LogOperation(operationType = "查询系统配置", operationContent = "查询系统参数配置")
    public Result<SystemParamConfigDTO> getSystemParamConfig() {
        try {
            SystemParamConfigDTO config = systemConfigService.getSystemParamConfig();
            return Result.success(config);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("更新系统参数配置")
    @PutMapping("/param")
    @LogOperation(operationType = "修改系统配置", operationContent = "更新系统参数配置")
    public Result<Void> updateSystemParamConfig(@RequestBody SystemParamConfigDTO config) {
        try {
            boolean success = systemConfigService.updateSystemParamConfig(config);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("更新系统参数配置失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("手动备份数据库")
    @PostMapping("/backup")
    @LogOperation(operationType = "数据备份", operationContent = "手动备份数据库")
    public Result<String> backupDatabase() {
        try {
            String backupFileName = systemConfigService.backupDatabase();
            return Result.success(backupFileName, "备份成功");
        } catch (Exception e) {
            return Result.error("备份失败: " + e.getMessage());
        }
    }
    
    @ApiOperation("获取备份历史列表")
    @GetMapping("/backup/history")
    @LogOperation(operationType = "查询备份历史", operationContent = "查询备份历史列表")
    public Result<List<BackupInfoDTO>> getBackupHistory() {
        try {
            List<BackupInfoDTO> backupList = systemConfigService.getBackupHistory();
            return Result.success(backupList);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("恢复数据库")
    @PostMapping("/backup/restore")
    @LogOperation(operationType = "数据恢复", operationContent = "从备份文件恢复数据库")
    public Result<String> restoreDatabase(@RequestParam String backupFileName) {
        try {
            boolean success = systemConfigService.restoreDatabase(backupFileName);
            if (success) {
                return Result.success("恢复成功");
            } else {
                return Result.error("恢复失败");
            }
        } catch (Exception e) {
            return Result.error("恢复失败: " + e.getMessage());
        }
    }
    
    @ApiOperation("删除备份文件")
    @DeleteMapping("/backup/{backupFileName}")
    @LogOperation(operationType = "删除备份", operationContent = "删除备份文件")
    public Result<String> deleteBackup(@PathVariable String backupFileName) {
        try {
            boolean success = systemConfigService.deleteBackup(backupFileName);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}

