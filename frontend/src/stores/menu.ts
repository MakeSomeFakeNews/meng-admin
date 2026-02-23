import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { MenuTreeNode } from '@/types/menu'
import type { RouteRecordRaw } from 'vue-router'
import router from '@/router'

// 动态导入所有 views 下的 Vue 组件
const modules = import.meta.glob('../views/**/*.vue')

export const useMenuStore = defineStore('menu', () => {
  /** 动态路由是否已加载 */
  const routesLoaded = ref(false)

  /** 菜单树数据（用于侧边栏渲染） */
  const menuTree = ref<MenuTreeNode[]>([])

  /**
   * 根据后端返回的菜单树生成动态路由并注册
   */
  function generateRoutes(menus: MenuTreeNode[]): void {
    menuTree.value = menus
    const routes = buildRoutes(menus)
    routes.forEach(route => {
      router.addRoute('Layout', route)
    })
    routesLoaded.value = true
  }

  /**
   * 递归构建路由配置
   */
  function buildRoutes(menus: MenuTreeNode[]): RouteRecordRaw[] {
    return menus
      .filter(menu => menu.menuType !== 2) // 过滤按钮类型
      .map(menu => {
        const route: RouteRecordRaw = {
          path: menu.path || '',
          name: menu.name || undefined,
          meta: {
            title: menu.title,
            icon: menu.icon,
            permission: menu.permission
          },
          component: resolveComponent(menu.component),
          children: menu.children ? buildRoutes(menu.children) : []
        }
        return route
      })
  }

  /**
   * 解析组件（动态导入）
   */
  function resolveComponent(component?: string): (() => Promise<unknown>) | undefined {
    if (!component) return undefined
    const key = `../views/${component}.vue`
    if (modules[key]) {
      return modules[key] as () => Promise<unknown>
    }
    // 组件未找到时返回 404 组件
    return modules['../views/error/404.vue'] as () => Promise<unknown>
  }

  /**
   * 重置菜单状态
   */
  function reset(): void {
    routesLoaded.value = false
    menuTree.value = []
  }

  return { routesLoaded, menuTree, generateRoutes, reset }
})
