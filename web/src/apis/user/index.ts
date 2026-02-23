import type * as T from './type'
import http from '@/utils/http'

export type * from './type'

/** 登录 */
export function login(data: { username: string, password: string }) {
  return http.post<T.LoginVO>('/auth/login', data)
}

/** 退出登录 */
export function logout() {
  return http.post('/auth/logout')
}

/** 获取当前用户信息 */
export const getUserInfo = () => {
  return http.get<T.LoginVO>('/auth/info')
}

/** 获取菜单路由 */
export const getUserRoutes = () => {
  return http.get<T.MenuTreeVO[]>('/menu/tree')
}
