import request from '@/utils/request'

/**
 * 统计报表相关API
 */
export const getStatistics = () => {
  return request({
    url: '/statistics/overview',
    method: 'get'
  })
}

export const getBookStatistics = () => {
  return request({
    url: '/statistics/book',
    method: 'get'
  })
}

export const getUserStatistics = () => {
  return request({
    url: '/statistics/user',
    method: 'get'
  })
}

export const getBorrowStatistics = () => {
  return request({
    url: '/statistics/borrow',
    method: 'get'
  })
}

export const getPopularBooks = (limit = 10) => {
  return request({
    url: '/statistics/popular-books',
    method: 'get',
    params: { limit }
  })
}

export const getActiveUsers = (limit = 10) => {
  return request({
    url: '/statistics/active-users',
    method: 'get',
    params: { limit }
  })
}

