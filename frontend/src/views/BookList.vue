<template>
  <div class="book-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>图书列表</span>
          <el-button type="primary" @click="handleAdd">添加图书</el-button>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="书名/作者/ISBN"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择" clearable style="width: 150px">
            <el-option
              v-for="category in categoryList"
              :key="category.categoryId"
              :label="category.categoryName"
              :value="category.categoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 图书列表 -->
      <el-table :data="bookList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="bookId" label="图书ID" width="150" />
        <el-table-column prop="title" label="书名" width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="publisher" label="出版社" width="150" show-overflow-tooltip />
        <el-table-column prop="isbn" label="ISBN" width="150" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            ￥{{ Number(scope.row.price).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="availableStock" label="可借" width="80" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="书名" prop="title">
          <el-input v-model="formData.title" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="formData.author" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="formData.publisher" />
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
          <el-input v-model="formData.isbn" :disabled="!!formData.bookId" />
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
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="formData.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as bookApi from '@/api/book'
import { getRootCategories } from '@/api/category'

const router = useRouter()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('编辑图书')
const formRef = ref(null)
const bookList = ref([])
const categoryList = ref([])

const searchForm = reactive({
  keyword: '',
  categoryId: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  bookId: '',
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
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
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

// 获取图书列表
const getBookList = async () => {
  loading.value = true
  try {
    const params = {
      keyword: searchForm.keyword,
      categoryId: searchForm.categoryId || undefined,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await bookApi.queryBooks(params)
    bookList.value = res.data || []
    // TODO: 后端返回总数
    pagination.total = res.data?.length || 0
  } catch (error) {
    ElMessage.error('获取图书列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  getBookList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.categoryId = ''
  handleSearch()
}

// 添加图书
const handleAdd = () => {
  router.push('/book/add')
}

// 编辑图书
const handleEdit = (row) => {
  dialogTitle.value = '编辑图书'
  Object.assign(formData, {
    bookId: row.bookId,
    title: row.title,
    author: row.author,
    publisher: row.publisher,
    publishDate: row.publishDate,
    isbn: row.isbn,
    categoryId: row.categoryId,
    price: Number(row.price),
    stock: row.stock,
    description: row.description || ''
  })
  dialogVisible.value = true
}

// 删除图书
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该图书吗？此操作不可恢复！', '警告', {
      type: 'warning'
    })
    await bookApi.deleteBook(row.bookId)
    ElMessage.success('删除成功')
    getBookList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const updateData = {
          title: formData.title,
          author: formData.author,
          publisher: formData.publisher,
          publishDate: formData.publishDate,
          categoryId: formData.categoryId,
          price: formData.price,
          stock: formData.stock,
          description: formData.description
        }
        await bookApi.updateBook(formData.bookId, updateData)
        ElMessage.success('保存成功')
        dialogVisible.value = false
        getBookList()
      } catch (error) {
        ElMessage.error(error.message || '保存失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 分页大小改变
const handleSizeChange = (val) => {
  pagination.pageSize = val
  getBookList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pagination.pageNum = val
  getBookList()
}

onMounted(() => {
  getCategoryList()
  getBookList()
})
</script>

<style scoped>
.book-list {
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

:deep(.el-card) {
  border-radius: 12px;
  border: none;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
}

:deep(.el-card:hover) {
  box-shadow: var(--shadow-md);
}

:deep(.el-card__header) {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #fafafa 0%, #ffffff 100%);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

:deep(.el-button--primary) {
  background: var(--gradient-primary);
  border: none;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.search-form {
  margin-bottom: 24px;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
}

:deep(.el-form-item) {
  margin-bottom: 0;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background: #fafafa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table tr:hover) {
  background-color: #f5f7fa;
}

:deep(.el-table td) {
  padding: 16px 0;
}

:deep(.el-button--small) {
  border-radius: 6px;
  padding: 8px 16px;
  transition: all 0.3s ease;
}

:deep(.el-button--small:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding: 20px 0;
}

:deep(.el-pagination) {
  justify-content: flex-end;
}

:deep(.el-dialog) {
  border-radius: 12px;
}

:deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-form {
    padding: 16px;
  }
  
  :deep(.el-table) {
    font-size: 12px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  :deep(.el-button--primary) {
    width: 100%;
  }
}
</style>

