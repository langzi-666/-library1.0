<template>
  <div class="register-container">
    <div class="register-box">
      <h2>用户注册</h2>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="4-20个字符，只能包含字母、数字和下划线"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="8-20个字符，必须包含大小写字母和数字"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input
            v-model="registerForm.name"
            placeholder="请输入真实姓名"
            prefix-icon="Avatar"
          />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="registerForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
            <el-radio label="保密">保密</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入11位手机号"
            prefix-icon="Phone"
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱地址"
            prefix-icon="Message"
          />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input
            v-model="registerForm.idCard"
            placeholder="请输入18位身份证号（可选）"
            prefix-icon="CreditCard"
          />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input
            v-model="registerForm.address"
            type="textarea"
            :rows="2"
            placeholder="请输入地址（可选）"
          />
        </el-form-item>
        <!-- 验证码输入框（暂时禁用，但保留代码） -->
        <el-form-item label="验证码" prop="captcha" v-if="false">
          <div class="captcha-container">
            <el-input
              v-model="registerForm.captcha"
              placeholder="请输入验证码"
              prefix-icon="Key"
              style="flex: 1"
            />
            <div class="captcha-image" @click="refreshCaptcha" v-if="captchaImage">
              <img :src="captchaImage" alt="验证码" />
            </div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleRegister"
            style="width: 100%"
          >
            注册
          </el-button>
        </el-form-item>
        <el-form-item>
          <div class="register-footer">
            <el-link type="primary" @click="router.push('/login')">已有账号？去登录</el-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import authApi from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const registerFormRef = ref(null)
const loading = ref(false)
const captchaImage = ref('')
const captchaKey = ref('')

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  gender: '保密',
  phone: '',
  email: '',
  idCard: '',
  address: '',
  captcha: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度必须在4-20个字符之间', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码长度必须在8-20个字符之间', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/, message: '密码必须包含大小写字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  idCard: [
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[0-9Xx]$/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  address: [
    { max: 200, message: '地址长度不能超过200个字符', trigger: 'blur' }
  ],
  // 验证码验证规则（暂时禁用，但保留代码）
  // captcha: [
  //   { required: true, message: '请输入验证码', trigger: 'blur' }
  // ]
}

// 获取验证码
const refreshCaptcha = async () => {
  try {
    const res = await authApi.getCaptcha()
    captchaImage.value = res.data.image
    captchaKey.value = res.data.key
  } catch (error) {
    ElMessage.error('获取验证码失败')
  }
}

// 注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const registerData = {
          ...registerForm,
          captchaKey: captchaKey.value
        }
        await userStore.register(registerData)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
        // 验证码暂时禁用，不再刷新
        // refreshCaptcha() // 刷新验证码
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  // 验证码暂时禁用，不再自动获取
  // refreshCaptcha()
})
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: var(--gradient-primary);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.register-container::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: backgroundMove 20s linear infinite;
}

@keyframes backgroundMove {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(50px, 50px);
  }
}

.register-box {
  width: 700px;
  max-width: 100%;
  padding: 50px 40px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 1;
  max-height: 90vh;
  overflow-y: auto;
  animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.register-box h2 {
  text-align: center;
  margin-bottom: 40px;
  color: #303133;
  font-size: 28px;
  font-weight: 600;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

:deep(.el-textarea__inner) {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

:deep(.el-textarea__inner:hover) {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-radio-group) {
  display: flex;
  gap: 20px;
}

:deep(.el-button--primary) {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  background: var(--gradient-primary);
  border: none;
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.4);
}

:deep(.el-button--primary:active) {
  transform: translateY(0);
}

.captcha-container {
  display: flex;
  gap: 12px;
  align-items: center;
}

.captcha-image {
  width: 120px;
  height: 40px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.captcha-image:hover {
  border-color: var(--primary-color);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.captcha-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.register-footer {
  width: 100%;
  text-align: center;
}

:deep(.el-link) {
  font-size: 14px;
  transition: all 0.3s ease;
}

:deep(.el-link:hover) {
  transform: translateY(-1px);
}

/* 滚动条美化 */
.register-box::-webkit-scrollbar {
  width: 6px;
}

.register-box::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.register-box::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.register-box::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-box {
    width: 95%;
    padding: 30px 24px;
  }
  
  .register-box h2 {
    font-size: 24px;
    margin-bottom: 30px;
  }
  
  :deep(.el-form-item__label) {
    width: 80px !important;
  }
}
</style>


