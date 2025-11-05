<template>
  <div class="statistics">
    <el-card shadow="hover" class="page-header">
      <h2>统计报表</h2>
      <p>查看系统的各项统计数据和分析报表</p>
    </el-card>

    <!-- 综合统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon blue">
              <el-icon :size="32"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overviewData.bookStatistics?.totalBooks || 0 }}</div>
              <div class="stat-title">图书总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon green">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overviewData.userStatistics?.totalUsers || 0 }}</div>
              <div class="stat-title">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon orange">
              <el-icon :size="32"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overviewData.borrowStatistics?.currentBorrowingCount || 0 }}</div>
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
              <div class="stat-value">{{ overviewData.borrowStatistics?.overdueBookCount || 0 }}</div>
              <div class="stat-title">逾期图书</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 借阅趋势图 -->
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>借阅趋势（最近30天）</span>
            </div>
          </template>
          <v-chart
            v-if="borrowTrendChartOption"
            :option="borrowTrendChartOption"
            style="height: 300px"
            autoresize
          />
        </el-card>
      </el-col>

      <!-- 图书分类分布图 -->
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>图书分类分布</span>
            </div>
          </template>
          <v-chart
            v-if="categoryChartOption"
            :option="categoryChartOption"
            style="height: 300px"
            autoresize
          />
        </el-card>
      </el-col>

      <!-- 用户增长趋势图 -->
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>用户增长趋势（最近30天）</span>
            </div>
          </template>
          <v-chart
            v-if="userTrendChartOption"
            :option="userTrendChartOption"
            style="height: 300px"
            autoresize
          />
        </el-card>
      </el-col>

      <!-- 图书状态分布图 -->
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>图书状态分布</span>
            </div>
          </template>
          <v-chart
            v-if="bookStatusChartOption"
            :option="bookStatusChartOption"
            style="height: 300px"
            autoresize
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 排行榜 -->
    <el-row :gutter="20" class="rankings-row">
      <!-- 热门图书排行 -->
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>热门图书TOP 10</span>
            </div>
          </template>
          <el-table :data="popularBooks" stripe>
            <el-table-column type="index" label="排名" width="60" />
            <el-table-column prop="title" label="书名" />
            <el-table-column prop="author" label="作者" width="120" />
            <el-table-column prop="borrowCount" label="借阅次数" width="100" align="center" />
          </el-table>
        </el-card>
      </el-col>

      <!-- 活跃用户排行 -->
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>活跃用户TOP 10</span>
            </div>
          </template>
          <el-table :data="activeUsers" stripe>
            <el-table-column type="index" label="排名" width="60" />
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="borrowCount" label="借阅次数" width="100" align="center" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细统计信息 -->
    <el-card shadow="hover" class="detail-stats">
      <template #header>
        <div class="card-header">
          <span>详细统计信息</span>
        </div>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="图书统计" name="book">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="图书总数">{{ bookStats.totalBooks }}</el-descriptions-item>
            <el-descriptions-item label="总库存">{{ bookStats.totalStock }}</el-descriptions-item>
            <el-descriptions-item label="在库数量">{{ bookStats.availableStock }}</el-descriptions-item>
            <el-descriptions-item label="已借出">{{ bookStats.borrowedStock }}</el-descriptions-item>
            <el-descriptions-item label="库存不足（<5本）">{{ bookStats.lowStockCount }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="用户统计" name="user">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户总数">{{ userStats.totalUsers }}</el-descriptions-item>
            <el-descriptions-item label="活跃用户（30天）">{{ userStats.activeUsers }}</el-descriptions-item>
            <el-descriptions-item label="正常用户">{{ getUserStatusCount('正常') }}</el-descriptions-item>
            <el-descriptions-item label="锁定用户">{{ getUserStatusCount('锁定') }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="借阅统计" name="borrow">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="总借阅次数">{{ borrowStats.totalBorrowCount }}</el-descriptions-item>
            <el-descriptions-item label="总归还次数">{{ borrowStats.totalReturnCount }}</el-descriptions-item>
            <el-descriptions-item label="归还率">{{ borrowStats.returnRate?.toFixed(2) }}%</el-descriptions-item>
            <el-descriptions-item label="逾期图书数">{{ borrowStats.overdueBookCount }}</el-descriptions-item>
            <el-descriptions-item label="逾期用户数">{{ borrowStats.overdueUserCount }}</el-descriptions-item>
            <el-descriptions-item label="逾期率">{{ borrowStats.overdueRate?.toFixed(2) }}%</el-descriptions-item>
            <el-descriptions-item label="平均逾期天数">{{ borrowStats.avgOverdueDays?.toFixed(2) }}天</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Reading, User, Document, Warning } from '@element-plus/icons-vue'
import * as statisticsApi from '@/api/statistics'

const overviewData = ref({})
const bookStats = ref({})
const userStats = ref({})
const borrowStats = ref({})
const popularBooks = ref([])
const activeUsers = ref([])
const activeTab = ref('book')

// 图表配置
const borrowTrendChartOption = ref(null)
const categoryChartOption = ref(null)
const userTrendChartOption = ref(null)
const bookStatusChartOption = ref(null)

// 获取用户状态数量
const getUserStatusCount = (status) => {
  return userStats.value.statusStatistics?.find(s => s.status === status)?.count || 0
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    // 加载综合统计
    const overviewRes = await statisticsApi.getStatistics()
    overviewData.value = overviewRes.data || {}
    
    // 加载各项详细统计
    const [bookRes, userRes, borrowRes, popularRes, activeRes] = await Promise.all([
      statisticsApi.getBookStatistics(),
      statisticsApi.getUserStatistics(),
      statisticsApi.getBorrowStatistics(),
      statisticsApi.getPopularBooks(10),
      statisticsApi.getActiveUsers(10)
    ])
    
    bookStats.value = bookRes.data || {}
    userStats.value = userRes.data || {}
    borrowStats.value = borrowRes.data || {}
    popularBooks.value = popularRes.data || []
    activeUsers.value = activeRes.data || []
    
    // 生成图表配置
    generateCharts()
  } catch (error) {
    ElMessage.error('加载统计数据失败：' + error.message)
  }
}

// 生成图表配置
const generateCharts = () => {
  // 借阅趋势图
  if (borrowStats.value.borrowTrend) {
    borrowTrendChartOption.value = {
      title: {
        text: '借阅趋势',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: borrowStats.value.borrowTrend.map(item => item.timeLabel)
      },
      yAxis: {
        type: 'value'
      },
      series: [{
        data: borrowStats.value.borrowTrend.map(item => item.value),
        type: 'line',
        smooth: true,
        areaStyle: {}
      }]
    }
  }
  
  // 分类分布图
  if (bookStats.value.categoryStatistics) {
    categoryChartOption.value = {
      title: {
        text: '分类分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      series: [{
        type: 'pie',
        radius: '60%',
        data: bookStats.value.categoryStatistics.map(item => ({
          value: item.bookCount,
          name: item.categoryName
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    }
  }
  
  // 用户增长趋势图
  if (userStats.value.newUsersTrend) {
    userTrendChartOption.value = {
      title: {
        text: '用户增长趋势',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: userStats.value.newUsersTrend.map(item => item.timeLabel)
      },
      yAxis: {
        type: 'value'
      },
      series: [{
        data: userStats.value.newUsersTrend.map(item => item.value),
        type: 'bar',
        itemStyle: {
          color: '#409EFF'
        }
      }]
    }
  }
  
  // 图书状态分布图
  if (bookStats.value.statusStatistics) {
    bookStatusChartOption.value = {
      title: {
        text: '状态分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      series: [{
        type: 'pie',
        radius: '60%',
        data: bookStats.value.statusStatistics.map(item => ({
          value: item.count,
          name: item.status
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    }
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.statistics {
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

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon.blue {
  background-color: #409EFF;
}

.stat-icon.green {
  background-color: #67C23A;
}

.stat-icon.orange {
  background-color: #E6A23C;
}

.stat-icon.red {
  background-color: #F56C6C;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}

.charts-row {
  margin-bottom: 20px;
}

.rankings-row {
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

.detail-stats {
  margin-bottom: 20px;
}
</style>

