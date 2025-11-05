<template>
  <div class="book-category">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>图书分类管理</span>
          <el-button type="primary" @click="handleAdd">添加分类</el-button>
        </div>
      </template>
      
      <!-- 分类树形表格 -->
      <el-table
        :data="categoryList"
        border
        style="width: 100%"
        v-loading="loading"
        row-key="categoryId"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        default-expand-all
      >
        <el-table-column prop="categoryName" label="分类名称" width="300" />
        <el-table-column prop="level" label="级别" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.level === 1 ? 'primary' : scope.row.level === 2 ? 'success' : 'info'">
              {{ scope.row.level }}级
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="formData.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父分类" prop="parentId">
          <el-select v-model="formData.parentId" style="width: 100%" placeholder="请选择父分类（0表示一级分类）">
            <el-option label="一级分类" :value="0" />
            <el-option
              v-for="category in flatCategoryList"
              :key="category.categoryId"
              :label="category.categoryName"
              :value="category.categoryId"
              :disabled="category.categoryId === formData.categoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分类级别" prop="level">
          <el-select v-model="formData.level" style="width: 100%" :disabled="!!formData.categoryId">
            <el-option label="1级" :value="1" />
            <el-option label="2级" :value="2" />
            <el-option label="3级" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入分类描述（可选）" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as categoryApi from '@/api/category'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const formRef = ref(null)
const categoryList = ref([])

const formData = reactive({
  categoryId: '',
  categoryName: '',
  parentId: 0,
  level: 1,
  description: '',
  sortOrder: 0
})

const formRules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  parentId: [{ required: true, message: '请选择父分类', trigger: 'change' }],
  level: [{ required: true, message: '请选择分类级别', trigger: 'change' }]
}

// 扁平化分类列表（用于父分类选择）
const flatCategoryList = computed(() => {
  const flatten = (list) => {
    let result = []
    list.forEach(item => {
      result.push(item)
      if (item.children && item.children.length > 0) {
        result = result.concat(flatten(item.children))
      }
    })
    return result
  }
  return flatten(categoryList.value)
})

// 获取分类列表
const getCategoryList = async () => {
  loading.value = true
  try {
    const res = await categoryApi.getAllCategoriesTree()
    categoryList.value = res.data || []
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

// 添加分类
const handleAdd = () => {
  dialogTitle.value = '添加分类'
  Object.assign(formData, {
    categoryId: '',
    categoryName: '',
    parentId: 0,
    level: 1,
    description: '',
    sortOrder: 0
  })
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row) => {
  dialogTitle.value = '编辑分类'
  Object.assign(formData, {
    categoryId: row.categoryId,
    categoryName: row.categoryName,
    parentId: row.parentId || 0,
    level: row.level,
    description: row.description || '',
    sortOrder: row.sortOrder || 0
  })
  dialogVisible.value = true
}

// 删除分类
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？此操作不可恢复！', '警告', {
      type: 'warning'
    })
    await categoryApi.deleteCategory(row.categoryId)
    ElMessage.success('删除成功')
    getCategoryList()
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
        if (formData.categoryId) {
          // 编辑
          const updateData = {
            categoryName: formData.categoryName,
            parentId: formData.parentId,
            level: formData.level,
            description: formData.description,
            sortOrder: formData.sortOrder
          }
          await categoryApi.updateCategory(formData.categoryId, updateData)
          ElMessage.success('更新成功')
        } else {
          // 添加
          const createData = {
            categoryName: formData.categoryName,
            parentId: formData.parentId,
            level: formData.level,
            description: formData.description,
            sortOrder: formData.sortOrder
          }
          await categoryApi.createCategory(createData)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        getCategoryList()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
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

onMounted(() => {
  getCategoryList()
})
</script>

<style scoped>
.book-category {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

