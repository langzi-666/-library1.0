<template>
  <div class="borrow-apply">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>借阅申请</span>
        </div>
      </template>
      
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="图书编号/ISBN" prop="bookIdOrIsbn">
          <el-input 
            v-model="form.bookIdOrIsbn" 
            placeholder="请输入图书编号或ISBN"
            clearable
            @keyup.enter="handleSearchBook"
          >
            <template #append>
              <el-button @click="handleSearchBook" :loading="searching">查询</el-button>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="借阅数量" prop="quantity">
          <el-input-number 
            v-model="form.quantity" 
            :min="1" 
            :max="3"
            :precision="0"
          />
          <span class="form-tip">同一图书最多借阅3本</span>
        </el-form-item>
        
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="form.remark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入备注信息（可选）"
          />
        </el-form-item>
        
        <!-- 图书信息展示 -->
        <el-form-item v-if="bookInfo" label="图书信息">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="书名">{{ bookInfo.title }}</el-descriptions-item>
            <el-descriptions-item label="作者">{{ bookInfo.author }}</el-descriptions-item>
            <el-descriptions-item label="出版社">{{ bookInfo.publisher }}</el-descriptions-item>
            <el-descriptions-item label="ISBN">{{ bookInfo.isbn }}</el-descriptions-item>
            <el-descriptions-item label="分类">{{ bookInfo.categoryName }}</el-descriptions-item>
            <el-descriptions-item label="价格">￥{{ bookInfo.price }}</el-descriptions-item>
            <el-descriptions-item label="可用库存">
              <el-tag :type="bookInfo.availableStock > 0 ? 'success' : 'danger'">
                {{ bookInfo.availableStock }} 本
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(bookInfo.status)">
                {{ bookInfo.status }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            提交借阅申请
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { borrowBook, getBookByIsbn, getBookById } from '@/api/book'
import { borrow } from '@/api/borrow'

const formRef = ref(null)
const searching = ref(false)
const submitting = ref(false)
const bookInfo = ref(null)

const form = reactive({
  bookIdOrIsbn: '',
  quantity: 1,
  remark: ''
})

const rules = {
  bookIdOrIsbn: [
    { required: true, message: '请输入图书编号或ISBN', trigger: 'blur' }
  ],
  quantity: [
    { required: true, message: '请输入借阅数量', trigger: 'blur' },
    { type: 'number', min: 1, max: 3, message: '借阅数量在1-3本之间', trigger: 'blur' }
  ]
}

// 查询图书信息
const handleSearchBook = async () => {
  if (!form.bookIdOrIsbn.trim()) {
    ElMessage.warning('请输入图书编号或ISBN')
    return
  }
  
  searching.value = true
  try {
    // 先尝试按bookId查询
    try {
      const res = await getBookById(form.bookIdOrIsbn)
      if (res.code === 200 && res.data) {
        bookInfo.value = res.data
        return
      }
    } catch (e) {
      // 继续尝试按ISBN查询
    }
    
    // 按ISBN查询
    const res = await getBookByIsbn(form.bookIdOrIsbn)
    if (res.code === 200 && res.data) {
      bookInfo.value = res.data
    } else {
      ElMessage.error('未找到该图书')
      bookInfo.value = null
    }
  } catch (error) {
    ElMessage.error('查询图书失败：' + (error.message || '未知错误'))
    bookInfo.value = null
  } finally {
    searching.value = false
  }
}

// 提交借阅申请
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    if (!bookInfo.value) {
      ElMessage.warning('请先查询并确认图书信息')
      return
    }
    
    if (bookInfo.value.availableStock < form.quantity) {
      ElMessage.error('可用库存不足')
      return
    }
    
    submitting.value = true
    try {
      const res = await borrow({
        bookIdOrIsbn: form.bookIdOrIsbn,
        quantity: form.quantity,
        remark: form.remark
      })
      
      if (res.code === 200) {
        ElMessage.success('借阅成功！')
        handleReset()
      } else {
        ElMessage.error(res.message || '借阅失败')
      }
    } catch (error) {
      ElMessage.error('借阅失败：' + (error.message || '未知错误'))
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const handleReset = () => {
  formRef.value?.resetFields()
  bookInfo.value = null
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    '在库': 'success',
    '已借出': 'warning',
    '已下架': 'info',
    '损坏': 'danger'
  }
  return statusMap[status] || 'info'
}
</script>

<style scoped>
.borrow-apply {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  font-weight: bold;
}

.form-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
</style>

