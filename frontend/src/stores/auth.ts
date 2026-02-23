import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi, getInfo } from '@/api/auth'
import type { LoginParams, LoginResult, UserAuthInfo } from '@/types/auth'
import type { MenuTreeNode } from '@/types/menu'

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref<string>(localStorage.getItem('token') ?? '')
  const userInfo = ref<UserAuthInfo['userInfo'] | null>(null)
  const permissions = ref<string[]>([])
  const roles = ref<string[]>([])
  const menus = ref<MenuTreeNode[]>([])

  // Getters
  const isLoggedIn = computed(() => !!token.value)

  /**
   * 判断是否拥有某个权限标识
   */
  function hasPermission(permissionCode: string): boolean {
    if (roles.value.includes('admin')) return true
    return permissions.value.includes(permissionCode)
  }

  /**
   * 登录
   */
  async function login(params: LoginParams): Promise<LoginResult> {
    const result = await loginApi(params)
    const tokenStr = `${result.tokenName}:${result.tokenValue}`
    token.value = tokenStr
    localStorage.setItem('token', tokenStr)
    return result
  }

  /**
   * 获取用户信息（登录后调用）
   */
  async function fetchUserInfo(): Promise<void> {
    const info = await getInfo()
    userInfo.value = info.userInfo
    permissions.value = info.permissions
    roles.value = info.roles
    menus.value = info.menus
  }

  /**
   * 退出登录
   */
  async function logout(): Promise<void> {
    try {
      await logoutApi()
    } finally {
      token.value = ''
      userInfo.value = null
      permissions.value = []
      roles.value = []
      menus.value = []
      localStorage.removeItem('token')
    }
  }

  /**
   * 重置 store（用于清理状态）
   */
  function reset(): void {
    token.value = ''
    userInfo.value = null
    permissions.value = []
    roles.value = []
    menus.value = []
    localStorage.removeItem('token')
  }

  return {
    token,
    userInfo,
    permissions,
    roles,
    menus,
    isLoggedIn,
    hasPermission,
    login,
    fetchUserInfo,
    logout,
    reset
  }
})
