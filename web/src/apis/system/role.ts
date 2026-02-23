import http from '@/utils/http'

export interface ListItem {
  id: number
  roleName: string
  roleCode: string
  sort: number
  status: number
  remark: string
  createTime: string
}

/** 获取角色分页列表 */
export function getRolePage(params?: { current?: number, size?: number, roleName?: string, roleCode?: string, status?: number | string }) {
  return http.get<PageRes<ListItem[]>>('/role/page', params)
}

/** 获取所有角色(不分页) */
export function getRoleList() {
  return http.get<ListItem[]>('/role/list')
}

/** 新增角色 */
export function addRole(data: { roleName: string, roleCode: string, sort?: number, status?: number, remark?: string }) {
  return http.post('/role', data)
}

/** 修改角色 */
export function updateRole(data: { id: number | string, roleName?: string, roleCode?: string, sort?: number, status?: number, remark?: string }) {
  return http.request({ method: 'put', url: '/role', data })
}

/** 删除角色 */
export function deleteRole(id: number | string) {
  return http.request({ method: 'delete', url: `/role/${id}` })
}

/** 获取角色菜单ID列表 */
export function getRoleMenuIds(roleId: number | string) {
  return http.get<number[]>(`/role/${roleId}/menus`)
}

/** 分配角色菜单权限 */
export function assignRoleMenus(data: { roleId: number | string, menuIds: number[] }) {
  return http.request({ method: 'put', url: '/role/assign-menus', data })
}

/** 兼容旧版 baseAPI 接口 */
export const baseAPI = {
  getList(params?: { current?: number, size?: number, page?: number, roleName?: string, status?: number | string }) {
    const { page, ...rest } = params || {}
    return getRolePage({ current: rest.current ?? page, ...rest })
  },
  getDetail(params: { id: number | string }) {
    return http.get<ListItem>(`/role/${params.id}`)
  },
  add(params: any) {
    return addRole(params)
  },
  update(params: any) {
    return updateRole(params)
  },
  delete(params: { ids: (string | number)[] }) {
    const promises = params.ids.map(id => deleteRole(id))
    return Promise.all(promises).then(() => ({ code: 200, data: true, message: '删除成功', success: true })) as any
  }
}
