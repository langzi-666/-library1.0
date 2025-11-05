<template>
  <div class="system-config">
    <el-card shadow="hover" class="page-header">
      <h2>系统设置</h2>
      <p>管理系统配置参数、借阅规则和数据备份</p>
    </el-card>

    <el-tabs v-model="activeTab" type="card" @tab-change="handleTabChange">
      <!-- 借阅规则设置 -->
      <el-tab-pane label="借阅规则" name="borrow-rule">
        <el-card shadow="hover">
          <template #header>
            <span>借阅规则配置</span>
          </template>
          <el-form :model="borrowRuleForm" :rules="borrowRuleRules" ref="borrowRuleFormRef" label-width="180px">
            <el-form-item label="借阅期限（天）" prop="borrowDays">
              <el-input-number
                v-model="borrowRuleForm.borrowDays"
                :min="1"
                :max="365"
                style="width: 200px"
              />
              <span class="form-tip">图书借阅的默认期限</span>
            </el-form-item>
            
            <el-form-item label="最大借阅数量" prop="maxBorrowCount">
              <el-input-number
                v-model="borrowRuleForm.maxBorrowCount"
                :min="1"
                :max="20"
                style="width: 200px"
              />
              <span class="form-tip">普通用户最多同时借阅的图书数量</span>
            </el-form-item>
            
            <el-form-item label="续借次数" prop="renewCount">
              <el-input-number
                v-model="borrowRuleForm.renewCount"
                :min="0"
                :max="5"
                style="width: 200px"
              />
              <span class="form-tip">每本图书最多可续借的次数</span>
            </el-form-item>
            
            <el-form-item label="续借期限（天）" prop="renewDays">
              <el-input-number
                v-model="borrowRuleForm.renewDays"
                :min="1"
                :max="365"
                style="width: 200px"
              />
              <span class="form-tip">每次续借延长的天数</span>
            </el-form-item>
            
            <el-form-item label="逾期费用（元/天）" prop="overdueFeePerDay">
              <el-input-number
                v-model="borrowRuleForm.overdueFeePerDay"
                :min="0"
                :max="100"
                :precision="2"
                style="width: 200px"
              />
              <span class="form-tip">图书逾期后每天的费用</span>
            </el-form-item>
            
            <el-form-item label="库存预警阈值" prop="stockWarningThreshold">
              <el-input-number
                v-model="borrowRuleForm.stockWarningThreshold"
                :min="0"
                :max="100"
                style="width: 200px"
              />
              <span class="form-tip">库存低于此值时系统提醒</span>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveBorrowRule" :loading="saving">保存配置</el-button>
              <el-button @click="resetBorrowRule">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 系统参数设置 -->
      <el-tab-pane label="系统参数" name="system-param">
        <el-card shadow="hover">
          <template #header>
            <span>系统参数配置</span>
          </template>
          <el-form :model="systemParamForm" :rules="systemParamRules" ref="systemParamFormRef" label-width="180px">
            <el-form-item label="系统名称" prop="systemName">
              <el-input
                v-model="systemParamForm.systemName"
                placeholder="请输入系统名称"
                style="width: 400px"
              />
            </el-form-item>
            
            <el-form-item label="系统版本" prop="systemVersion">
              <el-input
                v-model="systemParamForm.systemVersion"
                placeholder="请输入系统版本"
                style="width: 400px"
              />
            </el-form-item>
            
            <el-form-item label="系统公告" prop="systemNotice">
              <el-input
                v-model="systemParamForm.systemNotice"
                type="textarea"
                :rows="6"
                placeholder="请输入系统公告内容"
                style="width: 600px"
              />
            </el-form-item>
            
            <el-form-item label="系统Logo" prop="systemLogo">
              <el-input
                v-model="systemParamForm.systemLogo"
                placeholder="请输入Logo路径或URL"
                style="width: 400px"
              />
              <span class="form-tip">支持本地路径或URL</span>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveSystemParam" :loading="saving">保存配置</el-button>
              <el-button @click="resetSystemParam">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 数据备份与恢复 -->
      <el-tab-pane label="数据备份" name="backup">
        <el-card shadow="hover">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>数据备份与恢复</span>
              <el-button type="primary" @click="handleBackup" :loading="backupLoading">
                <el-icon><DocumentCopy /></el-icon>
                立即备份
              </el-button>
            </div>
          </template>
          
          <el-table :data="backupList" style="width: 100%" v-loading="backupListLoading">
            <el-table-column prop="fileName" label="备份文件名" width="300" />
            <el-table-column prop="backupTime" label="备份时间" width="180" />
            <el-table-column prop="backupType" label="备份类型" width="100" />
            <el-table-column prop="fileSize" label="文件大小" width="120">
              <template #default="{ row }">
                {{ formatFileSize(row.fileSize) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button
                  type="warning"
                  size="small"
                  @click="handleRestore(row.fileName)"
                  :loading="restoring"
                >
                  恢复
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  @click="handleDeleteBackup(row.fileName)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <el-empty v-if="!backupListLoading && backupList.length === 0" description="暂无备份记录" />
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DocumentCopy } from '@element-plus/icons-vue'
import {
  getBorrowRuleConfig,
  updateBorrowRuleConfig,
  getSystemParamConfig,
  updateSystemParamConfig,
  backupDatabase,
  getBackupHistory,
  restoreDatabase,
  deleteBackup
} from '@/api/system'

const activeTab = ref('borrow-rule')
const saving = ref(false)
const backupLoading = ref(false)
const backupListLoading = ref(false)
const restoring = ref(false)

// 借阅规则表单
const borrowRuleForm = reactive({
  borrowDays: 30,
  maxBorrowCount: 5,
  renewCount: 1,
  renewDays: 30,
  overdueFeePerDay: 1.00,
  stockWarningThreshold: 5
})

const borrowRuleRules = {
  borrowDays: [{ required: true, message: '请输入借阅期限', trigger: 'blur' }],
  maxBorrowCount: [{ required: true, message: '请输入最大借阅数量', trigger: 'blur' }],
  renewCount: [{ required: true, message: '请输入续借次数', trigger: 'blur' }],
  renewDays: [{ required: true, message: '请输入续借期限', trigger: 'blur' }],
  overdueFeePerDay: [{ required: true, message: '请输入逾期费用', trigger: 'blur' }],
  stockWarningThreshold: [{ required: true, message: '请输入库存预警阈值', trigger: 'blur' }]
}

const borrowRuleFormRef = ref(null)

// 系统参数表单
const systemParamForm = reactive({
  systemName: '图书管理系统',
  systemVersion: '1.0.0',
  systemNotice: '',
  systemLogo: ''
})

const systemParamRules = {
  systemName: [{ required: true, message: '请输入系统名称', trigger: 'blur' }],
  systemVersion: [{ required: true, message: '请输入系统版本', trigger: 'blur' }]
}

const systemParamFormRef = ref(null)

// 备份列表
const backupList = ref([])

// 加载借阅规则配置
const loadBorrowRuleConfig = async () => {
  try {
    const res = await getBorrowRuleConfig()
    if (res.code === 200) {
      Object.assign(borrowRuleForm, res.data)
    }
  } catch (error) {
    ElMessage.error('加载借阅规则配置失败')
  }
}

// 保存借阅规则配置
const saveBorrowRule = async () => {
  try {
    await borrowRuleFormRef.value.validate()
    saving.value = true
    const res = await updateBorrowRuleConfig(borrowRuleForm)
    if (res.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (error) {
    if (error !== false) {
      ElMessage.error('保存失败: ' + (error.message || error))
    }
  } finally {
    saving.value = false
  }
}

// 重置借阅规则表单
const resetBorrowRule = () => {
  loadBorrowRuleConfig()
}

// 加载系统参数配置
const loadSystemParamConfig = async () => {
  try {
    const res = await getSystemParamConfig()
    if (res.code === 200) {
      Object.assign(systemParamForm, res.data)
    }
  } catch (error) {
    ElMessage.error('加载系统参数配置失败')
  }
}

// 保存系统参数配置
const saveSystemParam = async () => {
  try {
    await systemParamFormRef.value.validate()
    saving.value = true
    const res = await updateSystemParamConfig(systemParamForm)
    if (res.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (error) {
    if (error !== false) {
      ElMessage.error('保存失败: ' + (error.message || error))
    }
  } finally {
    saving.value = false
  }
}

// 重置系统参数表单
const resetSystemParam = () => {
  loadSystemParamConfig()
}

// 加载备份列表
const loadBackupList = async () => {
  try {
    backupListLoading.value = true
    const res = await getBackupHistory()
    if (res.code === 200) {
      backupList.value = res.data || []
    } else {
      ElMessage.error(res.msg || '加载备份列表失败')
    }
  } catch (error) {
    ElMessage.error('加载备份列表失败: ' + (error.message || error))
  } finally {
    backupListLoading.value = false
  }
}

// 处理备份
const handleBackup = async () => {
  try {
    await ElMessageBox.confirm(
      '备份操作将创建当前数据库的快照，是否继续？',
      '确认备份',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    backupLoading.value = true
    const res = await backupDatabase()
    if (res.code === 200) {
      ElMessage.success('备份成功: ' + res.data)
      loadBackupList()
    } else {
      ElMessage.error(res.msg || '备份失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('备份失败: ' + (error.message || error))
    }
  } finally {
    backupLoading.value = false
  }
}

// 处理恢复
const handleRestore = async (fileName) => {
  try {
    await ElMessageBox.confirm(
      '恢复操作将覆盖当前数据库，此操作不可逆！是否继续？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    restoring.value = true
    const res = await restoreDatabase(fileName)
    if (res.code === 200) {
      ElMessage.success('恢复成功')
    } else {
      ElMessage.error(res.msg || '恢复失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('恢复失败: ' + (error.message || error))
    }
  } finally {
    restoring.value = false
  }
}

// 处理删除备份
const handleDeleteBackup = async (fileName) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该备份文件吗？',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await deleteBackup(fileName)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadBackupList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + (error.message || error))
    }
  }
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

// 标签页切换
const handleTabChange = (tabName) => {
  if (tabName === 'borrow-rule') {
    loadBorrowRuleConfig()
  } else if (tabName === 'system-param') {
    loadSystemParamConfig()
  } else if (tabName === 'backup') {
    loadBackupList()
  }
}

// 初始化
onMounted(() => {
  loadBorrowRuleConfig()
})
</script>

<style scoped>
.system-config {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.page-header p {
  margin: 8px 0 0 0;
  color: #909399;
  font-size: 14px;
}

.form-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}

:deep(.el-tabs__item) {
  font-size: 14px;
}

:deep(.el-form-item) {
  margin-bottom: 22px;
}
</style>

