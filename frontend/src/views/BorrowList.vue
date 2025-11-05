<template>
  <div class="borrow-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的借阅</span>
        </div>
      </template>
      
      <!-- 查询条件 -->
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="借阅中" value="借阅中" />
            <el-option label="已归还" value="已归还" />
            <el-option label="已逾期" value="已逾期" />
          </el-select>
        </el-form-item>
        <el-form-item label="图书名称">
          <el-input v-model="queryForm.bookTitle" placeholder="请输入图书名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 借阅记录列表 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="bookTitle" label="图书名称" width="200" />
        <el-table-column prop="bookAuthor" label="作者" width="150" />
        <el-table-column prop="bookIsbn" label="ISBN" width="150" />
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
        <el-table-column prop="returnDate" label="归还日期" width="180">
          <template #default="{ row }">
            {{ row.returnDate ? formatDate(row.returnDate) : '-' }}
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
            <span v-if="row.overdueFee > 0" style="color: red;">
              ￥{{ row.overdueFee }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === '借阅中'" 
              type="primary" 
              size="small"
              @click="handleRenew(row)"
            >
              续借
            </el-button>
            <el-button 
              v-if="row.status === '借阅中' || row.status === '已逾期'" 
              type="success" 
              size="small"
              @click="handleReturn(row)"
            >
              归还
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    
    <!-- 续借对话框 -->
    <el-dialog v-model="renewDialogVisible" title="续借图书" width="500px">
      <el-form :model="renewForm" label-width="100px">
        <el-form-item label="图书名称">
          <span>{{ renewForm.bookTitle }}</span>
        </el-form-item>
        <el-form-item label="当前应还日期">
          <span>{{ formatDate(renewForm.dueDate) }}</span>
        </el-form-item>
        <el-form-item label="续借天数">
          <el-input-number v-model="renewForm.days" :min="1" :max="30" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRenewSubmit" :loading="renewing">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 归还对话框 -->
    <el-dialog v-model="returnDialogVisible" title="归还图书" width="500px">
      <el-form :model="returnForm" label-width="100px">
        <el-form-item label="图书名称">
          <span>{{ returnForm.bookTitle }}</span>
        </el-form-item>
        <el-form-item label="借阅日期">
          <span>{{ formatDate(returnForm.borrowDate) }}</span>
        </el-form-item>
        <el-form-item label="应还日期">
          <span>{{ formatDate(returnForm.dueDate) }}</span>
        </el-form-item>
        <el-form-item label="图书状态" prop="bookStatus">
          <el-select v-model="returnForm.bookStatus" placeholder="请选择图书状态">
            <el-option label="正常" value="正常" />
            <el-option label="损坏" value="损坏" />
            <el-option label="丢失" value="丢失" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="returnForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReturnSubmit" :loading="returning">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyBorrowRecords, renew, returnBook } from '@/api/borrow'

const loading = ref(false)
const tableData = ref([])
const renewDialogVisible = ref(false)
const returnDialogVisible = ref(false)
const renewing = ref(false)
const returning = ref(false)

const queryForm = reactive({
  status: '',
  bookTitle: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const renewForm = reactive({
  recordId: null,
  bookTitle: '',
  dueDate: null,
  days: 30
})

const returnForm = reactive({
  recordIdOrBookId: '',
  bookTitle: '',
  borrowDate: null,
  dueDate: null,
  bookStatus: '正常',
  remark: ''
})

// 查询借阅记录
const handleQuery = () => {
  pagination.pageNum = 1
  loadData()
}

// 重置查询
const handleReset = () => {
  queryForm.status = ''
  queryForm.bookTitle = ''
  handleQuery()
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      ...queryForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getMyBorrowRecords(params)
    if (res.code === 200 && res.data) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 续借
const handleRenew = (row) => {
  renewForm.recordId = row.recordId
  renewForm.bookTitle = row.bookTitle
  renewForm.dueDate = row.dueDate
  renewForm.days = 30
  renewDialogVisible.value = true
}

// 提交续借
const handleRenewSubmit = async () => {
  renewing.value = true
  try {
    const res = await renew({
      recordId: renewForm.recordId,
      days: renewForm.days
    })
    if (res.code === 200) {
      ElMessage.success('续借成功！')
      renewDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '续借失败')
    }
  } catch (error) {
    ElMessage.error('续借失败：' + (error.message || '未知错误'))
  } finally {
    renewing.value = false
  }
}

// 归还
const handleReturn = (row) => {
  returnForm.recordIdOrBookId = row.recordId.toString()
  returnForm.bookTitle = row.bookTitle
  returnForm.borrowDate = row.borrowDate
  returnForm.dueDate = row.dueDate
  returnForm.bookStatus = '正常'
  returnForm.remark = ''
  returnDialogVisible.value = true
}

// 提交归还
const handleReturnSubmit = async () => {
  returning.value = true
  try {
    const res = await returnBook({
      recordIdOrBookId: returnForm.recordIdOrBookId,
      bookStatus: returnForm.bookStatus,
      remark: returnForm.remark
    })
    if (res.code === 200) {
      ElMessage.success('归还成功！')
      returnDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '归还失败')
    }
  } catch (error) {
    ElMessage.error('归还失败：' + (error.message || '未知错误'))
  } finally {
    returning.value = false
  }
}

// 分页
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.pageNum = page
  loadData()
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    '借阅中': 'success',
    '已归还': 'info',
    '已逾期': 'danger'
  }
  return statusMap[status] || 'info'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.borrow-list {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  font-weight: bold;
}

.query-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>

