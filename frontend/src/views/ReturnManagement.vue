<template>
  <div class="return-management">
    <el-card shadow="hover" class="page-header">
      <h2>归还管理</h2>
      <p>处理图书归还操作，支持单个归还和批量归还</p>
    </el-card>

    <el-tabs v-model="activeTab" class="tabs-container">
      <!-- 单个归还 -->
      <el-tab-pane label="单个归还" name="single">
        <el-card shadow="hover">
          <el-form :model="returnForm" :rules="returnRules" ref="returnFormRef" label-width="120px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="图书编号/ISBN" prop="recordIdOrBookId">
                  <el-input
                    v-model="returnForm.recordIdOrBookId"
                    placeholder="请输入图书编号、ISBN或借阅记录ID"
                    clearable
                    @keyup.enter="handleSearchRecord"
                  >
                    <template #append>
                      <el-button @click="handleSearchRecord" :loading="searching">
                        <el-icon><Search /></el-icon>
                      </el-button>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12" v-if="isAdmin">
                <el-form-item label="用户ID/用户名" prop="userId">
                  <el-input
                    v-model="returnForm.userId"
                    placeholder="管理员可指定用户（可选）"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 借阅记录信息 -->
            <el-card v-if="currentRecord" shadow="never" class="record-info">
              <template #header>
                <span>借阅记录信息</span>
              </template>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="图书名称">{{ currentRecord.bookTitle }}</el-descriptions-item>
                <el-descriptions-item label="作者">{{ currentRecord.bookAuthor }}</el-descriptions-item>
                <el-descriptions-item label="ISBN">{{ currentRecord.bookIsbn }}</el-descriptions-item>
                <el-descriptions-item label="借阅人">{{ currentRecord.userName }}</el-descriptions-item>
                <el-descriptions-item label="借阅日期">{{ formatDate(currentRecord.borrowDate) }}</el-descriptions-item>
                <el-descriptions-item label="应还日期">
                  <span :class="{ 'overdue-text': isOverdue }">
                    {{ formatDate(currentRecord.dueDate) }}
                  </span>
                </el-descriptions-item>
                <el-descriptions-item label="当前状态">
                  <el-tag :type="getStatusType(currentRecord.status)">
                    {{ currentRecord.status }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="逾期天数" v-if="overdueInfo.overdueDays > 0">
                  <span style="color: red; font-weight: bold;">
                    {{ overdueInfo.overdueDays }} 天
                  </span>
                </el-descriptions-item>
                <el-descriptions-item label="逾期费用" v-if="overdueInfo.overdueFee && overdueInfo.overdueFee > 0" :span="2">
                  <span style="color: red; font-weight: bold; font-size: 18px;">
                    ￥{{ Number(overdueInfo.overdueFee).toFixed(2) }}
                  </span>
                </el-descriptions-item>
              </el-descriptions>
            </el-card>

            <el-row :gutter="20" v-if="currentRecord">
              <el-col :span="12">
                <el-form-item label="图书状态" prop="bookStatus">
                  <el-radio-group v-model="returnForm.bookStatus">
                    <el-radio label="正常">正常</el-radio>
                    <el-radio label="损坏">损坏</el-radio>
                    <el-radio label="丢失">丢失</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="归还日期">
                  <el-date-picker
                    v-model="returnForm.returnDate"
                    type="datetime"
                    placeholder="选择归还日期"
                    format="YYYY-MM-DD HH:mm:ss"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    :default-value="new Date()"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="returnForm.remark"
                type="textarea"
                :rows="3"
                placeholder="请输入备注信息（如损坏说明、丢失原因等）"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleReturn" :loading="returning" :disabled="!currentRecord">
                确认归还
              </el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 批量归还 -->
      <el-tab-pane label="批量归还" name="batch">
        <el-card shadow="hover">
          <div class="batch-return-header">
            <el-alert
              title="批量归还说明"
              type="info"
              :closable="false"
              show-icon
            >
              <template #default>
                <p>1. 在下方表格中选择需要归还的借阅记录</p>
                <p>2. 点击"批量归还"按钮，系统将按"正常"状态批量归还</p>
                <p>3. 如需指定图书状态，请使用单个归还功能</p>
              </template>
            </el-alert>
          </div>

          <!-- 查询条件 -->
          <el-form :inline="true" :model="batchQueryForm" class="query-form">
            <el-form-item label="用户">
              <el-input v-model="batchQueryForm.userName" placeholder="用户名或姓名" clearable />
            </el-form-item>
            <el-form-item label="图书名称">
              <el-input v-model="batchQueryForm.bookTitle" placeholder="图书名称" clearable />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="batchQueryForm.status" placeholder="请选择状态" clearable>
                <el-option label="借阅中" value="借阅中" />
                <el-option label="已逾期" value="已逾期" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleBatchQuery">查询</el-button>
              <el-button @click="handleBatchReset">重置</el-button>
            </el-form-item>
          </el-form>

          <!-- 批量归还表格 -->
          <el-table
            :data="batchTableData"
            v-loading="batchLoading"
            stripe
            ref="batchTableRef"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" :selectable="checkSelectable" />
            <el-table-column prop="bookTitle" label="图书名称" width="200" />
            <el-table-column prop="bookAuthor" label="作者" width="150" />
            <el-table-column prop="userName" label="借阅人" width="120" />
            <el-table-column prop="borrowDate" label="借阅日期" width="180">
              <template #default="{ row }">
                {{ formatDate(row.borrowDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="dueDate" label="应还日期" width="180">
              <template #default="{ row }">
                <span :class="{ 'overdue-text': isRecordOverdue(row) }">
                  {{ formatDate(row.dueDate) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="overdueDays" label="逾期天数" width="100">
              <template #default="{ row }">
                <span v-if="row.overdueDays > 0" style="color: red;">
                  {{ row.overdueDays }} 天
                </span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="overdueFee" label="逾期费用" width="100">
              <template #default="{ row }">
                <span v-if="row.overdueFee && row.overdueFee > 0" style="color: red;">
                  ￥{{ Number(row.overdueFee).toFixed(2) }}
                </span>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination">
            <el-pagination
              v-model:current-page="batchPagination.pageNum"
              v-model:page-size="batchPagination.pageSize"
              :total="batchPagination.total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleBatchSizeChange"
              @current-change="handleBatchPageChange"
            />
          </div>

          <!-- 批量操作按钮 -->
          <div class="batch-actions">
            <el-button
              type="primary"
              :disabled="selectedRecords.length === 0"
              @click="handleBatchReturn"
              :loading="batchReturning"
            >
              批量归还（已选择 {{ selectedRecords.length }} 条）
            </el-button>
            <el-button @click="handleBatchSelectAll">全选当前页</el-button>
            <el-button @click="handleBatchClearSelection">清空选择</el-button>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 逾期记录 -->
      <el-tab-pane label="逾期记录" name="overdue">
        <el-card shadow="hover">
          <div class="overdue-header">
            <el-alert
              title="逾期记录"
              type="warning"
              :closable="false"
              show-icon
            >
              <template #default>
                <p>显示所有逾期未归还的图书记录，可批量处理归还操作</p>
              </template>
            </el-alert>
          </div>

          <!-- 查询条件 -->
          <el-form :inline="true" :model="overdueQueryForm" class="query-form">
            <el-form-item label="用户">
              <el-input v-model="overdueQueryForm.userName" placeholder="用户名或姓名" clearable />
            </el-form-item>
            <el-form-item label="图书名称">
              <el-input v-model="overdueQueryForm.bookTitle" placeholder="图书名称" clearable />
            </el-form-item>
            <el-form-item label="逾期天数">
              <el-input-number
                v-model="overdueQueryForm.minOverdueDays"
                :min="0"
                placeholder="最少天数"
                style="width: 120px"
              />
              <span style="margin: 0 10px">-</span>
              <el-input-number
                v-model="overdueQueryForm.maxOverdueDays"
                :min="0"
                placeholder="最多天数"
                style="width: 120px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleOverdueQuery">查询</el-button>
              <el-button @click="handleOverdueReset">重置</el-button>
            </el-form-item>
          </el-form>

          <!-- 逾期记录表格 -->
          <el-table
            :data="overdueTableData"
            v-loading="overdueLoading"
            stripe
            ref="overdueTableRef"
            @selection-change="handleOverdueSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="bookTitle" label="图书名称" width="200" />
            <el-table-column prop="bookAuthor" label="作者" width="150" />
            <el-table-column prop="userName" label="借阅人" width="120" />
            <el-table-column prop="borrowDate" label="借阅日期" width="180">
              <template #default="{ row }">
                {{ formatDate(row.borrowDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="dueDate" label="应还日期" width="180">
              <template #default="{ row }">
                {{ formatDate(row.dueDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="overdueDays" label="逾期天数" width="100" sortable>
              <template #default="{ row }">
                <span style="color: red; font-weight: bold;">
                  {{ row.overdueDays }} 天
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="overdueFee" label="逾期费用" width="100" sortable>
              <template #default="{ row }">
                <span style="color: red; font-weight: bold;">
                  ￥{{ row.overdueFee ? Number(row.overdueFee).toFixed(2) : '0.00' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleQuickReturn(row)">
                  归还
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination">
            <el-pagination
              v-model:current-page="overduePagination.pageNum"
              v-model:page-size="overduePagination.pageSize"
              :total="overduePagination.total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleOverdueSizeChange"
              @current-change="handleOverduePageChange"
            />
          </div>

          <!-- 批量操作按钮 -->
          <div class="batch-actions">
            <el-button
              type="primary"
              :disabled="selectedOverdueRecords.length === 0"
              @click="handleBatchReturnOverdue"
              :loading="batchReturning"
            >
              批量归还（已选择 {{ selectedOverdueRecords.length }} 条）
            </el-button>
            <el-button @click="handleOverdueSelectAll">全选当前页</el-button>
            <el-button @click="handleOverdueClearSelection">清空选择</el-button>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import * as borrowApi from '@/api/borrow'

const userStore = useUserStore()
const isAdmin = computed(() => {
  const role = userStore.userInfo?.role
  return role === '系统管理员' || role === '图书管理员'
})

const activeTab = ref('single')
const returnFormRef = ref(null)

// 单个归还表单
const returnForm = ref({
  recordIdOrBookId: '',
  userId: '',
  bookStatus: '正常',
  remark: '',
  returnDate: null
})

const returnRules = {
  recordIdOrBookId: [
    { required: true, message: '请输入图书编号、ISBN或借阅记录ID', trigger: 'blur' }
  ],
  bookStatus: [
    { required: true, message: '请选择图书状态', trigger: 'change' }
  ]
}

const searching = ref(false)
const returning = ref(false)
const currentRecord = ref(null)

// 计算逾期信息
const overdueInfo = computed(() => {
  if (!currentRecord.value) {
    return { overdueDays: 0, overdueFee: 0 }
  }
  
  const now = new Date()
  const dueDate = new Date(currentRecord.value.dueDate)
  
  if (now <= dueDate) {
    return { overdueDays: 0, overdueFee: 0 }
  }
  
  const overdueDays = Math.ceil((now - dueDate) / (1000 * 60 * 60 * 24))
  const overdueFee = overdueDays * 1.0 // 每天1元
  
  return { overdueDays, overdueFee }
})

const isOverdue = computed(() => {
  return overdueInfo.value.overdueDays > 0
})

// 批量归还
const batchQueryForm = ref({
  userName: '',
  bookTitle: '',
  status: '借阅中'
})

const batchTableData = ref([])
const batchLoading = ref(false)
const batchPagination = ref({
  pageNum: 1,
  pageSize: 20,
  total: 0
})
const selectedRecords = ref([])
const batchReturning = ref(false)
const batchTableRef = ref(null)
const overdueTableRef = ref(null)

// 逾期记录
const overdueQueryForm = ref({
  userName: '',
  bookTitle: '',
  minOverdueDays: null,
  maxOverdueDays: null
})

const overdueTableData = ref([])
const overdueLoading = ref(false)
const overduePagination = ref({
  pageNum: 1,
  pageSize: 20,
  total: 0
})
const selectedOverdueRecords = ref([])

// 搜索借阅记录
const handleSearchRecord = async () => {
  if (!returnForm.value.recordIdOrBookId) {
    ElMessage.warning('请输入图书编号、ISBN或借阅记录ID')
    return
  }
  
  searching.value = true
  try {
    // 先尝试作为借阅记录ID查询
    if (!isNaN(returnForm.value.recordIdOrBookId)) {
      try {
        const res = await borrowApi.getBorrowRecordById(parseInt(returnForm.value.recordIdOrBookId))
        if (res.code === 200 && res.data) {
          currentRecord.value = res.data
          // 设置用户ID（如果是管理员）
          if (isAdmin.value && !returnForm.value.userId) {
            returnForm.value.userId = res.data.userId
          }
          searching.value = false
          return
        }
      } catch (e) {
        // 不是记录ID，继续尝试其他方式
      }
    }
    
    // 作为图书编号或ISBN查询借阅记录
    const queryParams = {
      pageNum: 1,
      pageSize: 10,
      bookTitle: '',
      bookIsbn: returnForm.value.recordIdOrBookId,
      status: '借阅中'
    }
    
    if (isAdmin.value && returnForm.value.userId) {
      queryParams.userId = returnForm.value.userId
    }
    
    const res = await borrowApi.getBorrowRecords(queryParams)
    if (res.code === 200 && res.data && res.data.records && res.data.records.length > 0) {
      // 如果有多个记录，取第一个未归还的
      const record = res.data.records.find(r => r.status === '借阅中' || r.status === '已逾期') || res.data.records[0]
      currentRecord.value = record
      if (isAdmin.value && !returnForm.value.userId) {
        returnForm.value.userId = record.userId
      }
    } else {
      ElMessage.warning('未找到借阅记录，请检查输入的编号是否正确')
      currentRecord.value = null
    }
  } catch (error) {
    ElMessage.error('查询借阅记录失败：' + error.message)
    currentRecord.value = null
  } finally {
    searching.value = false
  }
}

// 执行归还
const handleReturn = async () => {
  if (!currentRecord.value) {
    ElMessage.warning('请先查询借阅记录')
    return
  }
  
  await returnFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      await ElMessageBox.confirm(
        `确认归还图书《${currentRecord.value.bookTitle}》吗？${isOverdue.value && overdueInfo.value.overdueFee ? `\n逾期费用：￥${Number(overdueInfo.value.overdueFee).toFixed(2)}` : ''}`,
        '确认归还',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      returning.value = true
      const returnData = {
        recordIdOrBookId: returnForm.value.recordIdOrBookId,
        userId: isAdmin.value ? returnForm.value.userId : undefined,
        bookStatus: returnForm.value.bookStatus,
        remark: returnForm.value.remark
      }
      
      const res = await borrowApi.returnBook(returnData)
      if (res.code === 200) {
        ElMessage.success('归还成功')
        handleReset()
        // 如果当前在批量归还或逾期记录标签页，刷新数据
        if (activeTab.value === 'batch') {
          handleBatchQuery()
        } else if (activeTab.value === 'overdue') {
          handleOverdueQuery()
        }
      }
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('归还失败：' + error.message)
      }
    } finally {
      returning.value = false
    }
  })
}

// 重置表单
const handleReset = () => {
  returnForm.value = {
    recordIdOrBookId: '',
    userId: '',
    bookStatus: '正常',
    remark: '',
    returnDate: null
  }
  currentRecord.value = null
  returnFormRef.value?.clearValidate()
}

// 批量归还查询
const handleBatchQuery = async () => {
  batchLoading.value = true
  try {
    const params = {
      pageNum: batchPagination.value.pageNum,
      pageSize: batchPagination.value.pageSize,
      userName: batchQueryForm.value.userName,
      bookTitle: batchQueryForm.value.bookTitle,
      status: batchQueryForm.value.status || undefined
    }
    
    const res = await borrowApi.getBorrowRecords(params)
    if (res.code === 200) {
      batchTableData.value = res.data.records || []
      batchPagination.value.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('查询失败：' + error.message)
  } finally {
    batchLoading.value = false
  }
}

const handleBatchReset = () => {
  batchQueryForm.value = {
    userName: '',
    bookTitle: '',
    status: '借阅中'
  }
  handleBatchQuery()
}

const handleSelectionChange = (selection) => {
  selectedRecords.value = selection
}

const checkSelectable = (row) => {
  return row.status === '借阅中' || row.status === '已逾期'
}

const handleBatchSelectAll = () => {
  if (batchTableRef.value) {
    batchTableData.value.forEach(row => {
      if (checkSelectable(row)) {
        batchTableRef.value.toggleRowSelection(row, true)
      }
    })
  }
}

const handleBatchClearSelection = () => {
  selectedRecords.value = []
  if (batchTableRef.value) {
    batchTableRef.value.clearSelection()
  }
}

const handleBatchSizeChange = (size) => {
  batchPagination.value.pageSize = size
  handleBatchQuery()
}

const handleBatchPageChange = (page) => {
  batchPagination.value.pageNum = page
  handleBatchQuery()
}

// 批量归还
const handleBatchReturn = async () => {
  if (selectedRecords.value.length === 0) {
    ElMessage.warning('请选择要归还的记录')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确认批量归还 ${selectedRecords.value.length} 条记录吗？`,
      '确认批量归还',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    batchReturning.value = true
    let successCount = 0
    let failCount = 0
    
    for (const record of selectedRecords.value) {
      try {
        const returnData = {
          recordIdOrBookId: record.recordId.toString(),
          bookStatus: '正常',
          remark: '批量归还'
        }
        await borrowApi.returnBook(returnData)
        successCount++
      } catch (error) {
        failCount++
        console.error(`归还失败：${record.bookTitle}`, error)
      }
    }
    
    ElMessage.success(`批量归还完成：成功 ${successCount} 条，失败 ${failCount} 条`)
    selectedRecords.value = []
    handleBatchQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量归还失败：' + error.message)
    }
  } finally {
    batchReturning.value = false
  }
}

// 逾期记录查询
const handleOverdueQuery = async () => {
  overdueLoading.value = true
  try {
    const params = {
      pageNum: overduePagination.value.pageNum,
      pageSize: overduePagination.value.pageSize,
      userName: overdueQueryForm.value.userName,
      bookTitle: overdueQueryForm.value.bookTitle,
      status: '已逾期'
    }
    
    const res = await borrowApi.getOverdueRecords(params)
    if (res.code === 200) {
      let records = res.data.records || []
      
      // 过滤逾期天数范围
      if (overdueQueryForm.value.minOverdueDays !== null) {
        records = records.filter(r => r.overdueDays >= overdueQueryForm.value.minOverdueDays)
      }
      if (overdueQueryForm.value.maxOverdueDays !== null) {
        records = records.filter(r => r.overdueDays <= overdueQueryForm.value.maxOverdueDays)
      }
      
      overdueTableData.value = records
      overduePagination.value.total = records.length
    }
  } catch (error) {
    ElMessage.error('查询失败：' + error.message)
  } finally {
    overdueLoading.value = false
  }
}

const handleOverdueReset = () => {
  overdueQueryForm.value = {
    userName: '',
    bookTitle: '',
    minOverdueDays: null,
    maxOverdueDays: null
  }
  handleOverdueQuery()
}

const handleOverdueSelectionChange = (selection) => {
  selectedOverdueRecords.value = selection
}

const handleOverdueSelectAll = () => {
  if (overdueTableRef.value) {
    overdueTableData.value.forEach(row => {
      overdueTableRef.value.toggleRowSelection(row, true)
    })
  }
}

const handleOverdueClearSelection = () => {
  selectedOverdueRecords.value = []
  if (overdueTableRef.value) {
    overdueTableRef.value.clearSelection()
  }
}

const handleOverdueSizeChange = (size) => {
  overduePagination.value.pageSize = size
  handleOverdueQuery()
}

const handleOverduePageChange = (page) => {
  overduePagination.value.pageNum = page
  handleOverdueQuery()
}

// 快速归还（从逾期记录）
const handleQuickReturn = async (row) => {
  returnForm.value.recordIdOrBookId = row.recordId.toString()
  returnForm.value.userId = row.userId
  currentRecord.value = row
  activeTab.value = 'single'
  
  // 自动触发归还
  setTimeout(() => {
    handleReturn()
  }, 500)
}

// 批量归还逾期记录
const handleBatchReturnOverdue = async () => {
  if (selectedOverdueRecords.value.length === 0) {
    ElMessage.warning('请选择要归还的记录')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确认批量归还 ${selectedOverdueRecords.value.length} 条逾期记录吗？`,
      '确认批量归还',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    batchReturning.value = true
    let successCount = 0
    let failCount = 0
    
    for (const record of selectedOverdueRecords.value) {
      try {
        const returnData = {
          recordIdOrBookId: record.recordId.toString(),
          bookStatus: '正常',
          remark: '批量归还逾期图书'
        }
        await borrowApi.returnBook(returnData)
        successCount++
      } catch (error) {
        failCount++
        console.error(`归还失败：${record.bookTitle}`, error)
      }
    }
    
    ElMessage.success(`批量归还完成：成功 ${successCount} 条，失败 ${failCount} 条`)
    selectedOverdueRecords.value = []
    handleOverdueQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量归还失败：' + error.message)
    }
  } finally {
    batchReturning.value = false
  }
}

// 工具函数
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const getStatusType = (status) => {
  const statusMap = {
    '借阅中': 'primary',
    '已归还': 'success',
    '已逾期': 'danger'
  }
  return statusMap[status] || 'info'
}

const isRecordOverdue = (record) => {
  if (!record.dueDate) return false
  const now = new Date()
  const dueDate = new Date(record.dueDate)
  return now > dueDate
}

// 初始化
onMounted(() => {
  if (activeTab.value === 'batch') {
    handleBatchQuery()
  } else if (activeTab.value === 'overdue') {
    handleOverdueQuery()
  }
})

// 监听标签页切换
watch(activeTab, (newTab) => {
  if (newTab === 'batch' && batchTableData.value.length === 0) {
    handleBatchQuery()
  } else if (newTab === 'overdue' && overdueTableData.value.length === 0) {
    handleOverdueQuery()
  }
})
</script>

<style scoped>
.return-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.tabs-container {
  margin-top: 20px;
}

.record-info {
  margin: 20px 0;
  background-color: #f5f7fa;
}

.overdue-text {
  color: red;
  font-weight: bold;
}

.query-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.batch-return-header,
.overdue-header {
  margin-bottom: 20px;
}

.batch-actions {
  margin-top: 20px;
  text-align: right;
}

.batch-actions .el-button {
  margin-left: 10px;
}
</style>

