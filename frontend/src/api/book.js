import request from '@/utils/request'

/**
 * 图书相关API
 */
export const getBookById = (bookId) => {
  return request({
    url: `/book/${bookId}`,
    method: 'get'
  })
}

export const getBookByIsbn = (isbn) => {
  return request({
    url: `/book/isbn/${isbn}`,
    method: 'get'
  })
}

export const queryBooks = (params) => {
  return request({
    url: '/book/query',
    method: 'get',
    params
  })
}

export const createBook = (data) => {
  return request({
    url: '/book',
    method: 'post',
    data
  })
}

export const updateBook = (bookId, data) => {
  return request({
    url: `/book/${bookId}`,
    method: 'put',
    data
  })
}

export const deleteBook = (bookId) => {
  return request({
    url: `/book/${bookId}`,
    method: 'delete'
  })
}

