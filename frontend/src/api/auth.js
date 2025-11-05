import request from '@/utils/request'

/**
 * 认证相关API
 */
export default {
  // 获取验证码
  getCaptcha() {
    return request({
      url: '/auth/captcha',
      method: 'get'
    })
  },
  
  // 用户注册
  register(data) {
    return request({
      url: '/auth/register',
      method: 'post',
      data
    })
  },
  
  // 用户登录
  login(data) {
    return request({
      url: '/auth/login',
      method: 'post',
      data
    })
  }
}


