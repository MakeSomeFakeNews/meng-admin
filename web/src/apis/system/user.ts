import http from '@/utils/http'

export interface ListItem {
  id: number
  username: string
  nickname: string
  avatar: string
  email: string
  phone: string
  status: number
  createTime: string
  updateTime: string
  roleIds: number[]
}

/** 获取用户分页列表 */
export function getUserPage(params?: { current?: number, size?: number, username?: string, nickname?: string, status?: number | string }) {
  return http.get<PageRes<ListItem[]>>('/user/page', params)
}

/** 获取用户详情 */
export function getUserDetail(id: number | string) {
  return http.get<ListItem>(`/user/${id}`)
}

/** 新增用户 */
export function addUser(data: { username: string, password: string, nickname: string, email?: string, phone?: string, status?: number, roleIds?: number[] }) {
  return http.post('/user', data)
}

/** 修改用户 */
export function updateUser(data: { id: number | string, nickname?: string, email?: string, phone?: string, status?: number }) {
  return http.request({ method: 'put', url: '/user', data })
}

/** 删除用户 */
export function deleteUser(id: number | string) {
  return http.request({ method: 'delete', url: `/user/${id}` })
}

/** 重置用户密码 */
export function resetUserPassword(data: { userId: number | string, newPassword: string }) {
  return http.request({ method: 'put', url: '/user/reset-password', data })
}

/** 修改用户状态 */
export function updateUserStatus(id: number | string, status: number) {
  return http.request({ method: 'put', url: `/user/${id}/status`, data: { status } })
}

/** 获取用户角色 */
export function getUserRoles(id: number | string) {
  return http.get<number[]>(`/user/${id}/roles`)
}

/** 分配用户角色 */
export function assignUserRoles(id: number | string, roleIds: number[]) {
  return http.request({ method: 'put', url: `/user/${id}/roles`, data: roleIds })
}

/** 兼容旧版 baseAPI 接口 (供现有视图使用) */
export const baseAPI = {
  getList(params?: { current?: number, size?: number, page?: number, username?: string, status?: number | string }) {
    const { page, ...rest } = params || {}
    return getUserPage({ current: rest.current ?? page, ...rest })
  },
  getDetail(params: { id: number | string }) {
    return getUserDetail(params.id)
  },
  add(params: any) {
    return addUser(params)
  },
  update(params: any) {
    return http.request({ method: 'put', url: '/user', data: params })
  },
  delete(params: { ids: (string | number)[] }) {
    // Backend only supports single delete; call for each id
    const promises = params.ids.map(id => deleteUser(id))
    return Promise.all(promises).then(() => ({ code: 200, data: true, message: '删除成功', success: true })) as any
  }
}
