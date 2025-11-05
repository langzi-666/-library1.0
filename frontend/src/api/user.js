import request from '@/utils/request'

/**
 * 用户相关API
 */
export default {
  // 获取当前用户信息
  getUserInfo() {
    return request({
      url: '/user/info',
      method: 'get'
    })
  },
  
  // 更新当前用户信息
  updateUserInfo(data) {
    return request({
      url: '/user/info',
      method: 'put',
      data
    })
  },
  
  // 更新用户信息（管理员）
  updateUserInfoById(userId, data) {
    return request({
      url: `/user/${userId}/info`,
      method: 'put',
      data
    })
  },
  
  // 修改密码
  changePassword(data) {
    return request({
      url: '/user/password',
      method: 'put',
      data
    })
  },
  
  // 获取用户列表
  getUserList(params) {
    return request({
      url: '/user/list',
      method: 'get',
      params
    })
  },
  
  // 根据ID获取用户信息
  getUserById(userId) {
    return request({
      url: `/user/${userId}`,
      method: 'get'
    })
  },
  
  // 删除用户
  deleteUser(userId) {
    return request({
      url: `/user/${userId}`,
      method: 'delete'
    })
  },
  
  // 锁定/解锁用户
  lockOrUnlockUser(userId, status) {
    return request({
      url: `/user/${userId}/status`,
      method: 'put',
      params: { status }
    })
  },
  
  // 重置用户密码
  resetPassword(userId, newPassword) {
    return request({
      url: `/user/${userId}/reset-password`,
      method: 'put',
      params: { newPassword }
    })
  }
}

