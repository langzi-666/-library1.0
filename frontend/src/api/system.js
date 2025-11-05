import request from '@/utils/request'

/**
 * 获取所有系统配置
 */
export function getAllConfigs() {
  return request({
    url: '/system/config/list',
    method: 'get'
  })
}

/**
 * 根据配置键获取配置值
 */
export function getConfigValue(configKey) {
  return request({
    url: `/system/config/${configKey}`,
    method: 'get'
  })
}

/**
 * 更新配置值
 */
export function updateConfig(configKey, configValue) {
  return request({
    url: `/system/config/${configKey}`,
    method: 'put',
    data: {
      configValue
    }
  })
}

/**
 * 批量更新配置
 */
export function batchUpdateConfigs(configs) {
  return request({
    url: '/system/config/batch',
    method: 'put',
    data: configs
  })
}

/**
 * 获取借阅规则配置
 */
export function getBorrowRuleConfig() {
  return request({
    url: '/system/borrow-rule',
    method: 'get'
  })
}

/**
 * 更新借阅规则配置
 */
export function updateBorrowRuleConfig(config) {
  return request({
    url: '/system/borrow-rule',
    method: 'put',
    data: config
  })
}

/**
 * 获取系统参数配置
 */
export function getSystemParamConfig() {
  return request({
    url: '/system/param',
    method: 'get'
  })
}

/**
 * 更新系统参数配置
 */
export function updateSystemParamConfig(config) {
  return request({
    url: '/system/param',
    method: 'put',
    data: config
  })
}

/**
 * 手动备份数据库
 */
export function backupDatabase() {
  return request({
    url: '/system/backup',
    method: 'post'
  })
}

/**
 * 获取备份历史列表
 */
export function getBackupHistory() {
  return request({
    url: '/system/backup/history',
    method: 'get'
  })
}

/**
 * 恢复数据库
 */
export function restoreDatabase(backupFileName) {
  return request({
    url: '/system/backup/restore',
    method: 'post',
    params: {
      backupFileName
    }
  })
}

/**
 * 删除备份文件
 */
export function deleteBackup(backupFileName) {
  return request({
    url: `/system/backup/${backupFileName}`,
    method: 'delete'
  })
}

