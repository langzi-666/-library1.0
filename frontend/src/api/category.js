import request from '@/utils/request'

/**
 * 图书分类相关API
 */
export const getCategoryById = (categoryId) => {
  return request({
    url: `/category/${categoryId}`,
    method: 'get'
  })
}

export const getAllCategoriesTree = () => {
  return request({
    url: '/category/tree',
    method: 'get'
  })
}

export const getRootCategories = () => {
  return request({
    url: '/category/root',
    method: 'get'
  })
}

export const getCategoriesByParentId = (parentId) => {
  return request({
    url: `/category/parent/${parentId}`,
    method: 'get'
  })
}

export const createCategory = (data) => {
  return request({
    url: '/category',
    method: 'post',
    data
  })
}

export const updateCategory = (categoryId, data) => {
  return request({
    url: `/category/${categoryId}`,
    method: 'put',
    data
  })
}

export const deleteCategory = (categoryId) => {
  return request({
    url: `/category/${categoryId}`,
    method: 'delete'
  })
}

