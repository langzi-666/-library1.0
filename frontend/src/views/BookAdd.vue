<template>
  <div class="book-add">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>添加图书</span>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </template>
      
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px" style="max-width: 800px">
        <el-form-item label="书名" prop="title">
          <el-input v-model="formData.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="formData.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="formData.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker
            v-model="formData.publishDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="formData.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="formData.categoryId" style="width: 100%" placeholder="请选择分类">
            <el-option
              v-for="category in categoryList"
              :key="category.categoryId"
              :label="category.categoryName"
              :value="category.categoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="formData.price" :precision="2" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <el-input-number v-model="formData.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="5" placeholder="请输入图书描述（可选）" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">提交</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="handleBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as bookApi from '@/api/book'
import { getRootCategories } from '@/api/category'

const router = useRouter()
const formRef = ref(null)
const submitLoading = ref(false)
const categoryList = ref([])

const formData = reactive({
  title: '',
  author: '',
  publisher: '',
  publishDate: '',
  isbn: '',
  categoryId: '',
  price: 0,
  stock: 0,
  description: ''
})

const formRules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  publisher: [{ required: true, message: '请输入出版社', trigger: 'blur' }],
  publishDate: [{ required: true, message: '请选择出版日期', trigger: 'change' }],
  isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
}

// 获取分类列表
const getCategoryList = async () => {
  try {
    const res = await getRootCategories()
    categoryList.value = res.data || []
  } catch (error) {
    console.error('获取分类列表失败：', error)
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await bookApi.createBook(formData)
        ElMessage.success('添加成功')
        router.push('/book/list')
      } catch (error) {
        ElMessage.error(error.message || '添加失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 重置表单
const handleReset = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    title: '',
    author: '',
    publisher: '',
    publishDate: '',
    isbn: '',
    categoryId: '',
    price: 0,
    stock: 0,
    description: ''
  })
}

// 返回
const handleBack = () => {
  router.push('/book/list')
}

onMounted(() => {
  getCategoryList()
})
</script>

<style scoped>
.book-add {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

