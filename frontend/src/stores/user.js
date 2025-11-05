import { defineStore } from 'pinia'
import { ref } from 'vue'
import authApi from '@/api/auth'
import userApi from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  
  // 登录
  const login = async (loginData) => {
    const res = await authApi.login(loginData)
    token.value = res.data.token
    userInfo.value = res.data.userInfo
    localStorage.setItem('token', token.value)
    return res
  }
  
  // 注册
  const register = async (registerData) => {
    const res = await authApi.register(registerData)
    return res
  }
  
  // 获取用户信息
  const getUserInfo = async () => {
    const res = await userApi.getUserInfo()
    userInfo.value = res.data
    return res
  }
  
  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }
  
  return {
    token,
    userInfo,
    login,
    register,
    getUserInfo,
    logout
  }
})


