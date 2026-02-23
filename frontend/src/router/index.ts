import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

// 静态路由（无需认证）
const staticRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在' }
  },
  {
    path: '/403',
    name: '403',
    component: () => import('@/views/error/403.vue'),
    meta: { title: '权限不足' }
  },
  {
    // 主布局路由（动态子路由会注册到此下）
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeOutlined' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  // 兜底路由
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: staticRoutes,
  scrollBehavior: () => ({ top: 0 })
})

// 路由白名单（无需认证）
const whiteList = ['/login', '/404', '/403']

router.beforeEach(async (to, _from, next) => {
  // 动态设置 document.title
  document.title = to.meta?.title ? `${to.meta.title} - 后台管理系统` : '后台管理系统'

  const token = localStorage.getItem('token')

  // 白名单路由直接放行
  if (whiteList.includes(to.path)) {
    // 已登录访问登录页，跳转首页
    if (token && to.path === '/login') {
      next('/')
    } else {
      next()
    }
    return
  }

  // 未登录跳转登录页
  if (!token) {
    next(`/login?redirect=${to.path}`)
    return
  }

  // 已登录，检查是否需要加载用户信息
  const { useAuthStore } = await import('@/stores/auth')
  const { useMenuStore } = await import('@/stores/menu')
  const authStore = useAuthStore()
  const menuStore = useMenuStore()

  if (!menuStore.routesLoaded) {
    try {
      // 加载用户信息和菜单
      await authStore.fetchUserInfo()
      // 生成并注册动态路由
      menuStore.generateRoutes(authStore.menus)
      // 重新导航到目标路由（路由刚注册需要重新跳转）
      next({ ...to, replace: true })
    } catch {
      // 加载失败（Token 失效），清理并跳转登录
      authStore.reset()
      menuStore.reset()
      next('/login')
    }
    return
  }

  next()
})

export default router
