<template>
  <div class="borrow-record-query">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>借阅记录查询</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 用户借阅记录查询 -->
        <el-tab-pane label="用户借阅记录" name="user">
          <el-card shadow="never">
            <!-- 查询条件 -->
            <el-form :inline="true" :model="userQueryForm" class="query-form">
              <el-form-item label="用户ID">
                <el-input 
                  v-model="userQueryForm.userId" 
                  placeholder="请输入用户ID" 
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="用户名">
                <el-input 
                  v-model="userQueryForm.userName" 
                  placeholder="请输入用户名" 
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="图书名称">
                <el-input 
                  v-model="userQueryForm.bookTitle" 
                  placeholder="请输入图书名称" 
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="userQueryForm.status" placeholder="请选择状态" clearable style="width: 150px">
                  <el-option label="借阅中" value="借阅中" />
                  <el-option label="已归还" value="已归还" />
                  <el-option label="已逾期" value="已逾期" />
                </el-select>
              </el-form-item>
              <el-form-item label="借阅日期">
                <el-date-picker
                  v-model="userDateRange"
                  type="datetimerange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  style="width: 350px"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUserQuery">查询</el-button>
                <el-button @click="handleUserReset">重置</el-button>
                <el-button type="success" @click="handleExportUserRecords" :loading="exporting">导出Excel</el-button>
              </el-form-item>
            </el-form>

            <!-- 用户借阅记录表格 -->
            <el-table
              :data="userTableData"
              v-loading="userLoading"
              stripe
              border
            >
              <el-table-column prop="recordId" label="记录ID" width="100" />
              <el-table-column prop="userName" label="用户名" width="120" />
              <el-table-column prop="bookTitle" label="图书名称" width="200" show-overflow-tooltip />
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
                  <span v-if="row.overdueDays > 0" style="color: red; font-weight: bold;">
                    {{ row.overdueDays }} 天
                  </span>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column prop="overdueFee" label="逾期费用" width="100">
                <template #default="{ row }">
                  <span v-if="row.overdueFee && row.overdueFee > 0" style="color: red; font-weight: bold;">
                    ￥{{ Number(row.overdueFee).toFixed(2) }}
                  </span>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="handleViewRecord(row)">
                    查看详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination">
              <el-pagination
                v-model:current-page="userPagination.pageNum"
                v-model:page-size="userPagination.pageSize"
                :total="userPagination.total"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleUserSizeChange"
                @current-change="handleUserPageChange"
              />
            </div>
          </el-card>
        </el-tab-pane>

        <!-- 图书借阅记录查询 -->
        <el-tab-pane label="图书借阅记录" name="book">
          <el-card shadow="never">
            <!-- 查询条件 -->
            <el-form :inline="true" :model="bookQueryForm" class="query-form">
              <el-form-item label="图书编号">
                <el-input 
                  v-model="bookQueryForm.bookId" 
                  placeholder="请输入图书编号" 
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="ISBN">
                <el-input 
                  v-model="bookQueryForm.isbn" 
                  placeholder="请输入ISBN" 
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleBookQuery">查询</el-button>
                <el-button @click="handleBookReset">重置</el-button>
              </el-form-item>
            </el-form>

            <!-- 图书信息卡片 -->
            <el-card v-if="currentBook" shadow="never" class="book-info-card">
              <el-descriptions title="图书信息" :column="3" border>
                <el-descriptions-item label="图书编号">{{ currentBook.bookId }}</el-descriptions-item>
                <el-descriptions-item label="书名">{{ currentBook.title }}</el-descriptions-item>
                <el-descriptions-item label="作者">{{ currentBook.author }}</el-descriptions-item>
                <el-descriptions-item label="ISBN">{{ currentBook.isbn }}</el-descriptions-item>
                <el-descriptions-item label="出版社">{{ currentBook.publisher }}</el-descriptions-item>
                <el-descriptions-item label="库存">{{ currentBook.stock }}</el-descriptions-item>
              </el-descriptions>
            </el-card>

            <!-- 图书借阅记录表格 -->
            <el-table
              :data="bookTableData"
              v-loading="bookLoading"
              stripe
              border
              style="margin-top: 20px"
            >
              <el-table-column prop="recordId" label="记录ID" width="100" />
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
              <el-table-column prop="renewCount" label="续借次数" width="100" />
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="handleViewRecord(row)">
                    查看详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 统计信息 -->
            <el-card v-if="bookStatistics" shadow="never" style="margin-top: 20px">
              <el-descriptions title="借阅统计" :column="4" border>
                <el-descriptions-item label="总借阅次数">{{ bookStatistics.totalBorrowCount }}</el-descriptions-item>
                <el-descriptions-item label="已归还次数">{{ bookStatistics.returnedCount }}</el-descriptions-item>
                <el-descriptions-item label="当前借阅中">{{ bookStatistics.borrowingCount }}</el-descriptions-item>
                <el-descriptions-item label="逾期次数">{{ bookStatistics.overdueCount }}</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-card>
        </el-tab-pane>

        <!-- 逾期记录查询 -->
        <el-tab-pane label="逾期记录" name="overdue">
          <el-card shadow="never">
            <!-- 查询条件 -->
            <el-form :inline="true" :model="overdueQueryForm" class="query-form">
              <el-form-item label="用户ID">
                <el-input 
                  v-model="overdueQueryForm.userId" 
                  placeholder="请输入用户ID" 
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="用户名">
                <el-input 
                  v-model="overdueQueryForm.userName" 
                  placeholder="请输入用户名" 
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="图书名称">
                <el-input 
                  v-model="overdueQueryForm.bookTitle" 
                  placeholder="请输入图书名称" 
                  clearable
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="逾期天数">
                <el-input-number 
                  v-model="overdueQueryForm.minOverdueDays" 
                  :min="0" 
                  placeholder="最小天数"
                  style="width: 120px"
                />
                <span style="margin: 0 10px">-</span>
                <el-input-number 
                  v-model="overdueQueryForm.maxOverdueDays" 
                  :min="0" 
                  placeholder="最大天数"
                  style="width: 120px"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleOverdueQuery">查询</el-button>
                <el-button @click="handleOverdueReset">重置</el-button>
                <el-button type="success" @click="handleExportOverdueRecords" :loading="exporting">导出Excel</el-button>
              </el-form-item>
            </el-form>

            <!-- 逾期记录表格 -->
            <el-table
              :data="overdueTableData"
              v-loading="overdueLoading"
              stripe
              border
            >
              <el-table-column prop="recordId" label="记录ID" width="100" />
              <el-table-column prop="userName" label="用户名" width="120" />
              <el-table-column prop="bookTitle" label="图书名称" width="200" show-overflow-tooltip />
              <el-table-column prop="bookAuthor" label="作者" width="150" />
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
              <el-table-column prop="overdueDays" label="逾期天数" width="120" sortable>
                <template #default="{ row }">
                  <span style="color: red; font-weight: bold;">
                    {{ row.overdueDays }} 天
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="overdueFee" label="逾期费用" width="120" sortable>
                <template #default="{ row }">
                  <span v-if="row.overdueFee && row.overdueFee > 0" style="color: red; font-weight: bold;">
                    ￥{{ Number(row.overdueFee).toFixed(2) }}
                  </span>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="handleViewRecord(row)">
                    查看详情
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
          </el-card>
        </el-tab-pane>

        <!-- 借阅统计分析 -->
        <el-tab-pane label="借阅统计分析" name="statistics">
          <el-card shadow="never">
            <!-- 统计卡片 -->
            <el-row :gutter="20" class="stats-cards">
              <el-col :xs="24" :sm="12" :md="6">
                <el-card class="stat-card">
                  <div class="stat-content">
                    <div class="stat-icon blue">
                      <el-icon :size="32"><Document /></el-icon>
                    </div>
                    <div class="stat-info">
                      <div class="stat-value">{{ borrowStats.totalBorrowCount || 0 }}</div>
                      <div class="stat-title">总借阅次数</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6">
                <el-card class="stat-card">
                  <div class="stat-content">
                    <div class="stat-icon green">
                      <el-icon :size="32"><Check /></el-icon>
                    </div>
                    <div class="stat-info">
                      <div class="stat-value">{{ borrowStats.totalReturnCount || 0 }}</div>
                      <div class="stat-title">总归还次数</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6">
                <el-card class="stat-card">
                  <div class="stat-content">
                    <div class="stat-icon orange">
                      <el-icon :size="32"><Reading /></el-icon>
                    </div>
                    <div class="stat-info">
                      <div class="stat-value">{{ borrowStats.currentBorrowingCount || 0 }}</div>
                      <div class="stat-title">当前借阅中</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6">
                <el-card class="stat-card">
                  <div class="stat-content">
                    <div class="stat-icon red">
                      <el-icon :size="32"><Warning /></el-icon>
                    </div>
                    <div class="stat-info">
                      <div class="stat-value">{{ borrowStats.overdueBookCount || 0 }}</div>
                      <div class="stat-title">逾期数量</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>

            <!-- 详细统计信息 -->
            <el-card shadow="never" style="margin-top: 20px">
              <el-descriptions title="借阅统计详情" :column="2" border>
                <el-descriptions-item label="归还率">
                  {{ borrowStats.returnRate ? borrowStats.returnRate.toFixed(2) + '%' : '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="逾期率">
                  {{ borrowStats.overdueRate ? borrowStats.overdueRate.toFixed(2) + '%' : '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="逾期用户数">
                  {{ borrowStats.overdueUserCount || 0 }}
                </el-descriptions-item>
                <el-descriptions-item label="平均逾期天数">
                  {{ borrowStats.avgOverdueDays ? borrowStats.avgOverdueDays.toFixed(2) + '天' : '-' }}
                </el-descriptions-item>
              </el-descriptions>
            </el-card>

            <!-- 热门图书排行 -->
            <el-card shadow="never" style="margin-top: 20px">
              <template #header>
                <div class="card-header">
                  <span>热门图书排行</span>
                </div>
              </template>
              <el-table :data="popularBooks" stripe border>
                <el-table-column type="index" label="排名" width="80" />
                <el-table-column prop="bookTitle" label="图书名称" width="200" />
                <el-table-column prop="bookAuthor" label="作者" width="150" />
                <el-table-column prop="borrowCount" label="借阅次数" width="120" sortable />
                <el-table-column prop="avgBorrowDays" label="平均借阅时长(天)" width="150" sortable>
                  <template #default="{ row }">
                    {{ row.avgBorrowDays ? row.avgBorrowDays.toFixed(2) : '-' }}
                  </template>
                </el-table-column>
                <el-table-column prop="borrowRate" label="借阅率" width="120" sortable>
                  <template #default="{ row }">
                    {{ row.borrowRate ? (row.borrowRate * 100).toFixed(2) + '%' : '-' }}
                  </template>
                </el-table-column>
              </el-table>
            </el-card>

            <!-- 活跃用户排行 -->
            <el-card shadow="never" style="margin-top: 20px">
              <template #header>
                <div class="card-header">
                  <span>活跃用户排行</span>
                </div>
              </template>
              <el-table :data="activeUsers" stripe border>
                <el-table-column type="index" label="排名" width="80" />
                <el-table-column prop="userName" label="用户名" width="150" />
                <el-table-column prop="name" label="姓名" width="150" />
                <el-table-column prop="borrowCount" label="借阅次数" width="120" sortable />
                <el-table-column prop="totalBorrowDays" label="累计借阅时长(天)" width="150" sortable>
                  <template #default="{ row }">
                    {{ row.totalBorrowDays ? row.totalBorrowDays.toFixed(2) : '-' }}
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 记录详情对话框 -->
    <el-dialog v-model="recordDetailVisible" title="借阅记录详情" width="800px">
      <el-descriptions v-if="currentRecord" :column="2" border>
        <el-descriptions-item label="记录ID">{{ currentRecord.recordId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRecord.status)">
            {{ currentRecord.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentRecord.userName }}</el-descriptions-item>
        <el-descriptions-item label="图书名称">{{ currentRecord.bookTitle }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ currentRecord.bookAuthor }}</el-descriptions-item>
        <el-descriptions-item label="ISBN">{{ currentRecord.bookIsbn }}</el-descriptions-item>
        <el-descriptions-item label="借阅日期">{{ formatDate(currentRecord.borrowDate) }}</el-descriptions-item>
        <el-descriptions-item label="应还日期">{{ formatDate(currentRecord.dueDate) }}</el-descriptions-item>
        <el-descriptions-item label="归还日期">
          {{ currentRecord.returnDate ? formatDate(currentRecord.returnDate) : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="续借次数">{{ currentRecord.renewCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="逾期天数">
          <span v-if="currentRecord.overdueDays > 0" style="color: red; font-weight: bold;">
            {{ currentRecord.overdueDays }} 天
          </span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="逾期费用">
          <span v-if="currentRecord.overdueFee && currentRecord.overdueFee > 0" style="color: red; font-weight: bold;">
            ￥{{ Number(currentRecord.overdueFee).toFixed(2) }}
          </span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="图书状态">{{ currentRecord.bookStatus || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentRecord.remark || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Check, Reading, Warning } from '@element-plus/icons-vue'
import { 
  getBorrowRecords, 
  getUserBorrowRecords, 
  getBookBorrowRecords, 
  getOverdueRecords,
  getBorrowRecordById 
} from '@/api/borrow'
import { getBorrowStatistics, getPopularBooks, getActiveUsers } from '@/api/statistics'
import * as bookApi from '@/api/book'

const activeTab = ref('user')

// 用户借阅记录查询
const userQueryForm = reactive({
  userId: '',
  userName: '',
  bookTitle: '',
  status: ''
})
const userDateRange = ref(null)
const userTableData = ref([])
const userLoading = ref(false)
const userPagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

// 图书借阅记录查询
const bookQueryForm = reactive({
  bookId: '',
  isbn: ''
})
const bookTableData = ref([])
const bookLoading = ref(false)
const currentBook = ref(null)
const bookStatistics = ref(null)

// 逾期记录查询
const overdueQueryForm = reactive({
  userId: '',
  userName: '',
  bookTitle: '',
  minOverdueDays: null,
  maxOverdueDays: null
})
const overdueTableData = ref([])
const overdueLoading = ref(false)
const overduePagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

// 借阅统计分析
const borrowStats = ref({})
const popularBooks = ref([])
const activeUsers = ref([])

// 记录详情
const recordDetailVisible = ref(false)
const currentRecord = ref(null)

// 导出状态
const exporting = ref(false)

// 监听日期范围变化
watch(userDateRange, (val) => {
  if (val && val.length === 2) {
    userQueryForm.borrowDateStart = val[0]
    userQueryForm.borrowDateEnd = val[1]
  } else {
    userQueryForm.borrowDateStart = null
    userQueryForm.borrowDateEnd = null
  }
})

// 用户借阅记录查询
const handleUserQuery = () => {
  userPagination.pageNum = 1
  loadUserRecords()
}

const handleUserReset = () => {
  userQueryForm.userId = ''
  userQueryForm.userName = ''
  userQueryForm.bookTitle = ''
  userQueryForm.status = ''
  userDateRange.value = null
  handleUserQuery()
}

const loadUserRecords = async () => {
  userLoading.value = true
  try {
    const params = {
      ...userQueryForm,
      pageNum: userPagination.pageNum,
      pageSize: userPagination.pageSize
    }
    // 移除空值
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })
    
    const res = await getBorrowRecords(params)
    if (res.code === 200 && res.data) {
      userTableData.value = res.data.records || []
      userPagination.total = res.data.total || 0
    } else {
      ElMessage.error(res.message || '查询失败')
    }
  } catch (error) {
    ElMessage.error('查询失败：' + (error.message || '未知错误'))
  } finally {
    userLoading.value = false
  }
}

const handleUserSizeChange = (size) => {
  userPagination.pageSize = size
  loadUserRecords()
}

const handleUserPageChange = (page) => {
  userPagination.pageNum = page
  loadUserRecords()
}

// 图书借阅记录查询
const handleBookQuery = async () => {
  if (!bookQueryForm.bookId && !bookQueryForm.isbn) {
    ElMessage.warning('请输入图书编号或ISBN')
    return
  }
  
  bookLoading.value = true
  try {
    // 先查询图书信息
    if (bookQueryForm.bookId) {
      const bookRes = await bookApi.getBookById(bookQueryForm.bookId)
      if (bookRes.code === 200 && bookRes.data) {
        currentBook.value = bookRes.data
      }
    } else if (bookQueryForm.isbn) {
      const bookRes = await bookApi.getBookByIsbn(bookQueryForm.isbn)
      if (bookRes.code === 200 && bookRes.data) {
        currentBook.value = bookRes.data
        bookQueryForm.bookId = bookRes.data.bookId
      }
    }
    
    // 查询借阅记录
    if (currentBook.value) {
      const res = await getBookBorrowRecords(currentBook.value.bookId)
      if (res.code === 200 && res.data) {
        bookTableData.value = res.data || []
        
        // 计算统计信息
        bookStatistics.value = {
          totalBorrowCount: bookTableData.value.length,
          returnedCount: bookTableData.value.filter(r => r.status === '已归还').length,
          borrowingCount: bookTableData.value.filter(r => r.status === '借阅中' || r.status === '已逾期').length,
          overdueCount: bookTableData.value.filter(r => r.status === '已逾期').length
        }
      } else {
        ElMessage.error(res.message || '查询失败')
      }
    }
  } catch (error) {
    ElMessage.error('查询失败：' + (error.message || '未知错误'))
  } finally {
    bookLoading.value = false
  }
}

const handleBookReset = () => {
  bookQueryForm.bookId = ''
  bookQueryForm.isbn = ''
  currentBook.value = null
  bookTableData.value = []
  bookStatistics.value = null
}

// 逾期记录查询
const handleOverdueQuery = () => {
  overduePagination.pageNum = 1
  loadOverdueRecords()
}

const handleOverdueReset = () => {
  overdueQueryForm.userId = ''
  overdueQueryForm.userName = ''
  overdueQueryForm.bookTitle = ''
  overdueQueryForm.minOverdueDays = null
  overdueQueryForm.maxOverdueDays = null
  handleOverdueQuery()
}

const loadOverdueRecords = async () => {
  overdueLoading.value = true
  try {
    const params = {
      userId: overdueQueryForm.userId || undefined,
      bookTitle: overdueQueryForm.bookTitle || undefined,
      pageNum: overduePagination.pageNum,
      pageSize: overduePagination.pageSize
    }
    
    // 移除空值
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })
    
    const res = await getOverdueRecords(params)
    if (res.code === 200 && res.data) {
      let records = res.data.records || []
      
      // 客户端筛选逾期天数范围
      if (overdueQueryForm.minOverdueDays !== null && overdueQueryForm.minOverdueDays !== undefined) {
        records = records.filter(r => r.overdueDays >= overdueQueryForm.minOverdueDays)
      }
      if (overdueQueryForm.maxOverdueDays !== null && overdueQueryForm.maxOverdueDays !== undefined) {
        records = records.filter(r => r.overdueDays <= overdueQueryForm.maxOverdueDays)
      }
      
      overdueTableData.value = records
      overduePagination.total = records.length
    } else {
      ElMessage.error(res.message || '查询失败')
    }
  } catch (error) {
    ElMessage.error('查询失败：' + (error.message || '未知错误'))
  } finally {
    overdueLoading.value = false
  }
}

const handleOverdueSizeChange = (size) => {
  overduePagination.pageSize = size
  loadOverdueRecords()
}

const handleOverduePageChange = (page) => {
  overduePagination.pageNum = page
  loadOverdueRecords()
}

// 借阅统计分析
const loadBorrowStatistics = async () => {
  try {
    const [statsRes, popularRes, activeRes] = await Promise.all([
      getBorrowStatistics(),
      getPopularBooks(10),
      getActiveUsers(10)
    ])
    
    if (statsRes.code === 200 && statsRes.data) {
      borrowStats.value = statsRes.data
    }
    
    if (popularRes.code === 200 && popularRes.data) {
      popularBooks.value = popularRes.data
    }
    
    if (activeRes.code === 200 && activeRes.data) {
      activeUsers.value = activeRes.data
    }
  } catch (error) {
    ElMessage.error('加载统计数据失败：' + (error.message || '未知错误'))
  }
}

// 查看记录详情
const handleViewRecord = async (row) => {
  try {
    const res = await getBorrowRecordById(row.recordId)
    if (res.code === 200 && res.data) {
      currentRecord.value = res.data
      recordDetailVisible.value = true
    } else {
      ElMessage.error(res.message || '获取记录详情失败')
    }
  } catch (error) {
    ElMessage.error('获取记录详情失败：' + (error.message || '未知错误'))
  }
}

// 导出Excel
const handleExportUserRecords = async () => {
  exporting.value = true
  try {
    ElMessage.info('导出功能开发中...')
    // TODO: 实现导出功能
  } catch (error) {
    ElMessage.error('导出失败：' + (error.message || '未知错误'))
  } finally {
    exporting.value = false
  }
}

const handleExportOverdueRecords = async () => {
  exporting.value = true
  try {
    ElMessage.info('导出功能开发中...')
    // TODO: 实现导出功能
  } catch (error) {
    ElMessage.error('导出失败：' + (error.message || '未知错误'))
  } finally {
    exporting.value = false
  }
}

// 标签页切换
const handleTabChange = (tabName) => {
  if (tabName === 'user' && userTableData.value.length === 0) {
    loadUserRecords()
  } else if (tabName === 'statistics') {
    loadBorrowStatistics()
  }
}

// 格式化日期
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
  loadUserRecords()
})
</script>

<style scoped>
.borrow-record-query {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

.query-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.book-info-card {
  margin-bottom: 20px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  color: white;
}

.stat-icon.blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.green {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.orange {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.red {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-title {
  font-size: 14px;
  color: #666;
}
</style>

