import request from '@/utils/request'

/**
 * 借阅相关API
 */
export const borrow = (data) => {
  return request({
    url: '/borrow',
    method: 'post',
    data
  })
}

export const returnBook = (data) => {
  return request({
    url: '/borrow/return',
    method: 'post',
    data
  })
}

export const renew = (data) => {
  return request({
    url: '/borrow/renew',
    method: 'post',
    data
  })
}

export const getMyBorrowRecords = (params) => {
  return request({
    url: '/borrow/my',
    method: 'get',
    params
  })
}

export const getBorrowRecords = (params) => {
  return request({
    url: '/borrow/list',
    method: 'get',
    params
  })
}

export const getUserBorrowRecords = (userId, params) => {
  return request({
    url: `/borrow/user/${userId}`,
    method: 'get',
    params
  })
}

export const getBookBorrowRecords = (bookId) => {
  return request({
    url: `/borrow/book/${bookId}`,
    method: 'get'
  })
}

export const getOverdueRecords = (params) => {
  return request({
    url: '/borrow/overdue',
    method: 'get',
    params
  })
}

export const getBorrowRecordById = (recordId) => {
  return request({
    url: `/borrow/${recordId}`,
    method: 'get'
  })
}

