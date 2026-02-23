import { get, post } from '@/utils/request'
import type { LoginParams, LoginResult, UserAuthInfo } from '@/types/auth'

/**
 * 用户登录
 */
export function login(data: LoginParams): Promise<LoginResult> {
  return post<LoginResult>('/auth/login', data)
}

/**
 * 退出登录
 */
export function logout(): Promise<void> {
  return post<void>('/auth/logout')
}

/**
 * 获取当前用户信息（权限、菜单等）
 */
export function getInfo(): Promise<UserAuthInfo> {
  return get<UserAuthInfo>('/auth/info')
}
