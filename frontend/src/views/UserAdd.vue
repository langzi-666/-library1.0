<template>
  <div class="user-add">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>添加用户</span>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </template>
      
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px" style="max-width: 800px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="4-20个字符，只能包含字母、数字和下划线" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="8-20个字符，必须包含大小写字母和数字" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="formData.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
            <el-radio label="保密">保密</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入11位手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" style="width: 100%" placeholder="请选择角色">
            <el-option label="系统管理员" value="系统管理员" />
            <el-option label="图书管理员" value="图书管理员" />
            <el-option label="普通用户" value="普通用户" />
          </el-select>
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="formData.idCard" placeholder="请输入18位身份证号（可选）" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" type="textarea" :rows="3" placeholder="请输入地址（可选）" />
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import authApi from '@/api/auth'

const router = useRouter()
const formRef = ref(null)
const submitLoading = ref(false)

const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  gender: '保密',
  phone: '',
  email: '',
  role: '普通用户',
  idCard: '',
  address: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== formData.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度为4-20个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码长度为8-20个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/, message: '密码必须包含大小写字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const registerData = {
          username: formData.username,
          password: formData.password,
          name: formData.name,
          gender: formData.gender,
          phone: formData.phone,
          email: formData.email,
          idCard: formData.idCard || undefined,
          address: formData.address || undefined,
          role: formData.role
        }
        await authApi.register(registerData)
        ElMessage.success('添加成功')
        router.push('/user')
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
    username: '',
    password: '',
    confirmPassword: '',
    name: '',
    gender: '保密',
    phone: '',
    email: '',
    role: '普通用户',
    idCard: '',
    address: ''
  })
}

// 返回
const handleBack = () => {
  router.push('/user')
}
</script>

<style scoped>
.user-add {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

