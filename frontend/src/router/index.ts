import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/login/index.vue'),
      meta: { public: true }
    },
    {
      path: '/',
      name: 'layout',
      component: () => import('../views/layout/index.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: '/dashboard',
          name: 'dashboard',
          component: () => import('../views/dashboard/index.vue'),
          meta: { title: '首页' }
        },
        {
          path: '/error-questions',
          name: 'errorQuestions',
          component: () => import('../views/error-question/index.vue'),
          meta: { title: '错题管理' }
        },
        {
          path: '/error-question/create',
          name: 'createErrorQuestion',
          component: () => import('../views/error-question/create.vue'),
          meta: { title: '录入错题' }
        },
        {
          path: '/error-question/detail/:id',
          name: 'errorQuestionDetail',
          component: () => import('../views/error-question/detail.vue'),
          meta: { title: '错题详情' }
        },
        {
          path: '/review',
          name: 'review',
          component: () => import('../views/review/index.vue'),
          meta: { title: '复习计划' }
        },
        {
          path: '/analysis',
          name: 'analysis',
          component: () => import('../views/analysis/index.vue'),
          meta: { title: '学情分析' }
        },
        {
          path: '/shares',
          name: 'shares',
          component: () => import('../views/share/index.vue'),
          meta: { title: '错题分享' }
        },
        {
          path: '/share/detail/:shareId',
          name: 'shareDetail',
          component: () => import('../views/share/share-detail.vue'),
          meta: { title: '分享详情' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 初始化用户信息
  if (!userStore.userInfo) {
    userStore.initUserInfo()
  }
  
  if (to.meta.public) {
    // 公开页面，直接访问
    next()
  } else if (!userStore.isLoggedIn) {
    // 需要登录但未登录，跳转到登录页
    next('/login')
  } else {
    // 已登录，允许访问
    next()
  }
})

export default router
