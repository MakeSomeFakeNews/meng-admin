import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

/**
 * 静态路由（不需要权限）
 */
const staticRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', requiresAuth: false }
  }
]

/**
 * 动态路由基础布局（认证后挂载）
 */
export const layoutRoute: RouteRecordRaw = {
  path: '/',
  name: 'Layout',
  component: () => import('@/views/layout/index.vue'),
  redirect: '/dashboard',
  meta: { requiresAuth: true },
  children: [
    {
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index.vue'),
      meta: { title: '首页', icon: 'HomeOutlined', requiresAuth: true }
    }
  ]
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [...staticRoutes, layoutRoute],
  scrollBehavior: () => ({ top: 0 })
})

/**
 * 全局路由守卫：鉴权 + 加载用户信息
 */
router.beforeEach(async (to, _from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth !== false

  if (!requiresAuth) {
    // 已登录时访问登录页，重定向到首页
    if (to.path === '/login' && authStore.isLoggedIn) {
      return next({ path: '/' })
    }
    return next()
  }

  // 未登录，跳转登录页
  if (!authStore.isLoggedIn) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  // 已登录但尚未加载用户信息
  if (!authStore.userInfo) {
    try {
      await authStore.fetchUserInfo()
    } catch {
      authStore.reset()
      return next({ path: '/login' })
    }
  }

  next()
})

export default router
