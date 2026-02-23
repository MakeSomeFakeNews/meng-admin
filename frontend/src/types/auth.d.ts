import type { MenuTreeNode } from './menu'

/**
 * 登录参数
 */
export interface LoginParams {
  username: string
  password: string
}

/**
 * 登录响应
 */
export interface LoginResult {
  tokenName: string
  tokenValue: string
  userInfo: {
    id: number
    username: string
    nickname: string
    avatar: string
  }
  permissions: string[]
  roles: string[]
  menus: MenuTreeNode[]
}

/**
 * 当前用户信息（/auth/info 响应）
 */
export interface UserAuthInfo {
  userInfo: {
    id: number
    username: string
    nickname: string
    avatar: string
    email: string
    phone: string
  }
  permissions: string[]
  roles: string[]
  menus: MenuTreeNode[]
}
