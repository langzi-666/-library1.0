<template>
  <div class="dashboard">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ userInfo?.name || userInfo?.username || '用户' }}！</h2>
          <p>今天是 {{ currentDate }}，祝您使用愉快！</p>
        </div>
        <div class="welcome-icon">
          <el-icon :size="80" color="#667eea"><Reading /></el-icon>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in stats" :key="stat.title">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="32"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card class="quick-actions-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>快捷操作</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="8" :md="6" v-for="action in quickActions" :key="action.title">
          <div class="quick-action-item" @click="handleQuickAction(action.path)">
            <el-icon :size="32" :color="action.color"><component :is="action.icon" /></el-icon>
            <span>{{ action.title }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 最近动态 -->
    <el-row :gutter="20">
      <el-col :xs="24" :md="12">
        <el-card class="recent-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近借阅</span>
              <el-button type="text" @click="$router.push('/borrow/list')">查看更多</el-button>
            </div>
          </template>
          <div class="empty-placeholder">
            <el-empty description="暂无借阅记录" :image-size="100" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="recent-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
            </div>
          </template>
          <div class="empty-placeholder">
            <el-empty description="暂无公告" :image-size="100" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  Reading, 
  Document, 
  User, 
  Collection,
  Plus,
  Search,
  List
} from '@element-plus/icons-vue'
import * as statisticsApi from '@/api/statistics'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)

// 图标映射
const iconMap = {
  Reading,
  Document,
  User,
  Collection,
  Plus,
  Search,
  List
}

// 当前日期
const currentDate = computed(() => {
  const date = new Date()
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const weekday = weekdays[date.getDay()]
  return `${year}年${month}月${day}日 ${weekday}`
})

// 统计数据
const stats = ref([
  {
    title: '总图书数',
    value: '0',
    icon: Reading,
    color: '#409EFF'
  },
  {
    title: '在借图书',
    value: '0',
    icon: Document,
    color: '#67C23A'
  },
  {
    title: '总用户数',
    value: '0',
    icon: User,
    color: '#E6A23C'
  },
  {
    title: '待处理',
    value: '0',
    icon: Collection,
    color: '#F56C6C'
  }
])

// 快捷操作
const quickActions = ref([
  {
    title: '图书查询',
    icon: Search,
    color: '#409EFF',
    path: '/book/list'
  },
  {
    title: '借阅申请',
    icon: Document,
    color: '#67C23A',
    path: '/borrow/apply'
  },
  {
    title: '我的借阅',
    icon: List,
    color: '#E6A23C',
    path: '/borrow/list'
  },
  {
    title: '添加图书',
    icon: Plus,
    color: '#F56C6C',
    path: '/book/add'
  }
])

// 处理快捷操作
const handleQuickAction = (path) => {
  router.push(path)
}

// 获取统计数据
onMounted(async () => {
  try {
    const res = await statisticsApi.getStatistics()
    const data = res.data || {}
    
    // 更新统计卡片数据
    stats.value[0].value = data.bookStatistics?.totalBooks || 0
    stats.value[1].value = data.borrowStatistics?.currentBorrowingCount || 0
    stats.value[2].value = data.userStatistics?.totalUsers || 0
    stats.value[3].value = data.borrowStatistics?.overdueBookCount || 0
  } catch (error) {
    console.error('获取统计数据失败：', error)
  }
})
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.welcome-card {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.welcome-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.welcome-text h2 {
  margin: 0 0 12px 0;
  font-size: 26px;
  font-weight: 600;
  color: #303133;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-text p {
  margin: 0;
  color: #909399;
  font-size: 15px;
}

.welcome-icon {
  opacity: 0.2;
  transition: all 0.3s ease;
}

.welcome-card:hover .welcome-icon {
  opacity: 0.3;
  transform: scale(1.05);
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
  border: none;
}

.stat-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-hover);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 0;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.stat-card:hover .stat-icon {
  transform: scale(1.1) rotate(5deg);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 6px;
  line-height: 1.2;
  transition: all 0.3s ease;
}

.stat-card:hover .stat-value {
  color: var(--primary-color);
}

.stat-title {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

.quick-actions-card {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

:deep(.card-header .el-button) {
  padding: 0;
  font-weight: normal;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #fafafa 0%, #f5f5f5 100%);
  border: 1px solid transparent;
  position: relative;
  overflow: hidden;
}

.quick-action-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s ease;
}

.quick-action-item:hover::before {
  left: 100%;
}

.quick-action-item:hover {
  background: linear-gradient(135deg, #f0f2f5 0%, #e8ebed 100%);
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
  border-color: rgba(64, 158, 255, 0.2);
}

.quick-action-item .el-icon {
  transition: all 0.3s ease;
}

.quick-action-item:hover .el-icon {
  transform: scale(1.1);
}

.quick-action-item span {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  transition: color 0.3s ease;
}

.quick-action-item:hover span {
  color: var(--primary-color);
}

.recent-card {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.recent-card:hover {
  box-shadow: var(--shadow-md);
}

.empty-placeholder {
  min-height: 240px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 16px;
}

:deep(.el-empty__description) {
  color: #909399;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .dashboard {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
  
  .welcome-icon {
    margin-top: 0;
  }
  
  .welcome-text h2 {
    font-size: 22px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .stat-icon {
    width: 56px;
    height: 56px;
  }
  
  .quick-action-item {
    padding: 20px 16px;
  }
}

@media (max-width: 480px) {
  .welcome-text h2 {
    font-size: 20px;
  }
  
  .stat-content {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
  
  .stat-icon {
    width: 48px;
    height: 48px;
  }
}
</style>
