import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/Layout.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: {
      title: '登录'
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: {
      title: '注册'
    }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: {
          title: '首页',
          requiresAuth: true
        }
      },
      {
        path: '/user',
        name: 'UserManagement',
        component: () => import('@/views/UserManagement.vue'),
        meta: {
          title: '用户管理',
          requiresAuth: true
        }
      },
      {
        path: '/user/add',
        name: 'UserAdd',
        component: () => import('@/views/UserAdd.vue'),
        meta: {
          title: '添加用户',
          requiresAuth: true
        }
      },
      {
        path: '/book/list',
        name: 'BookList',
        component: () => import('@/views/BookList.vue'),
        meta: {
          title: '图书列表',
          requiresAuth: true
        }
      },
      {
        path: '/book/add',
        name: 'BookAdd',
        component: () => import('@/views/BookAdd.vue'),
        meta: {
          title: '添加图书',
          requiresAuth: true
        }
      },
      {
        path: '/book/category',
        name: 'BookCategory',
        component: () => import('@/views/BookCategory.vue'),
        meta: {
          title: '图书分类',
          requiresAuth: true
        }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: {
          title: '个人中心',
          requiresAuth: true
        }
      },
      {
        path: '/borrow/apply',
        name: 'BorrowApply',
        component: () => import('@/views/BorrowApply.vue'),
        meta: {
          title: '借阅申请',
          requiresAuth: true
        }
      },
      {
        path: '/borrow/list',
        name: 'BorrowList',
        component: () => import('@/views/BorrowList.vue'),
        meta: {
          title: '我的借阅',
          requiresAuth: true
        }
      },
      {
        path: '/statistics',
        name: 'Statistics',
        component: () => import('@/views/Statistics.vue'),
        meta: {
          title: '统计报表',
          requiresAuth: true
        }
      },
      {
        path: '/borrow/return',
        name: 'ReturnManagement',
        component: () => import('@/views/ReturnManagement.vue'),
        meta: {
          title: '归还管理',
          requiresAuth: true
        }
      },
      {
        path: '/borrow/query',
        name: 'BorrowRecordQuery',
        component: () => import('@/views/BorrowRecordQuery.vue'),
        meta: {
          title: '借阅记录查询',
          requiresAuth: true
        }
      },
      {
        path: '/system/config',
        name: 'SystemConfig',
        component: () => import('@/views/SystemConfig.vue'),
        meta: {
          title: '系统设置',
          requiresAuth: true
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || '图书管理系统'
  
  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next('/login')
      return
    }
  }
  
  next()
})

export default router

