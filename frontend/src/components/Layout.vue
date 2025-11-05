<template>
  <el-container class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="layout-header">
      <div class="header-left">
        <h1 class="system-title">图书管理系统</h1>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-icon><User /></el-icon>
            <span>{{ userInfo?.name || userInfo?.username || '用户' }}</span>
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="layout-aside">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          class="sidebar-menu"
        >
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          
          <el-sub-menu index="book">
            <template #title>
              <el-icon><Reading /></el-icon>
              <span>图书管理</span>
            </template>
            <el-menu-item index="/book/list">图书列表</el-menu-item>
            <el-menu-item index="/book/add">添加图书</el-menu-item>
            <el-menu-item index="/book/category">图书分类</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="borrow">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>借阅管理</span>
            </template>
            <el-menu-item index="/borrow/list">借阅记录</el-menu-item>
            <el-menu-item index="/borrow/apply">借阅申请</el-menu-item>
            <el-menu-item index="/borrow/return">归还管理</el-menu-item>
            <el-menu-item index="/borrow/query" v-if="isAdmin">借阅记录查询</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="user" v-if="isAdmin">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/user">用户列表</el-menu-item>
            <el-menu-item index="/user/add">添加用户</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/statistics" v-if="isAdmin">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>统计报表</template>
          </el-menu-item>

          <el-menu-item index="/system/config" v-if="userInfo?.role === '系统管理员'">
            <el-icon><Tools /></el-icon>
            <template #title>系统设置</template>
          </el-menu-item>

          <el-menu-item index="/profile">
            <el-icon><Setting /></el-icon>
            <template #title>个人中心</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { House, Reading, Document, User, Setting, ArrowDown, DataAnalysis, Tools } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapse = ref(false)
const activeMenu = computed(() => route.path)

const userInfo = computed(() => userStore.userInfo)
const isAdmin = computed(() => {
  return userInfo.value?.role === '系统管理员' || userInfo.value?.role === '图书管理员'
})

// 下拉菜单命令处理
const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch {
      // 用户取消
    }
  } else if (command === 'profile') {
    router.push('/profile')
  }
}

// 获取用户信息
onMounted(async () => {
  if (!userInfo.value && userStore.token) {
    try {
      await userStore.getUserInfo()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.layout-header {
  background: var(--gradient-primary);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: var(--shadow-md);
  position: relative;
  z-index: 1000;
}

.layout-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-left .system-title {
  font-size: 22px;
  font-weight: 600;
  margin: 0;
  letter-spacing: 0.5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-size: 14px;
}

.user-info:hover {
  background-color: rgba(255, 255, 255, 0.15);
  transform: translateY(-1px);
}

.user-info .el-icon {
  font-size: 18px;
}

.layout-aside {
  background-color: #fff;
  border-right: 1px solid #e4e7ed;
  transition: width 0.3s ease;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 60px);
  overflow-y: auto;
  overflow-x: hidden;
  padding: 8px 0;
}

:deep(.el-menu-item) {
  border-radius: 8px;
  margin: 4px 8px;
  transition: all 0.3s ease;
}

:deep(.el-menu-item:hover) {
  background-color: #f0f2f5 !important;
  transform: translateX(4px);
}

:deep(.el-menu-item.is-active) {
  background: var(--gradient-primary) !important;
  color: white !important;
}

:deep(.el-menu-item.is-active .el-icon) {
  color: white !important;
}

:deep(.el-sub-menu__title) {
  border-radius: 8px;
  margin: 4px 8px;
  transition: all 0.3s ease;
}

:deep(.el-sub-menu__title:hover) {
  background-color: #f0f2f5 !important;
}

:deep(.el-sub-menu .el-menu-item) {
  padding-left: 50px !important;
}

/* 滚动条美化 */
.sidebar-menu::-webkit-scrollbar {
  width: 4px;
}

.sidebar-menu::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.sidebar-menu::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.layout-main {
  background-color: #f5f7fa;
  padding: 24px;
  overflow-y: auto;
  position: relative;
}

/* 路由过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .layout-header {
    padding: 0 16px;
  }
  
  .header-left .system-title {
    font-size: 18px;
  }
  
  .layout-main {
    padding: 16px;
  }
  
  .layout-aside {
    position: absolute;
    left: 0;
    top: 60px;
    height: calc(100vh - 60px);
    z-index: 999;
    box-shadow: 2px 0 12px rgba(0, 0, 0, 0.15);
  }
}
</style>

