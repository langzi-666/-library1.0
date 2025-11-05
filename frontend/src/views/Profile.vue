<template>
  <div class="profile">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>
      
      <el-descriptions :column="2" border v-if="userInfo">
        <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ userInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ userInfo.role }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ userInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ userInfo.status }}</el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">{{ formatDate(userInfo.createTime) }}</el-descriptions-item>
      </el-descriptions>
      
      <div v-else class="loading-placeholder">
        <el-skeleton :rows="5" animated />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

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
.profile {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  font-weight: bold;
}

.loading-placeholder {
  padding: 20px;
}
</style>

