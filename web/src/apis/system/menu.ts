import http from '@/utils/http'

export interface ListItem {
  id: number
  parentId: number
  title: string
  name: string
  path: string
  component: string
  icon: string
  menuType: number
  permission: string
  sort: number
  visible: number
  status: number
  children: ListItem[]
}

export interface MenuOptionsItem {
  id: number
  title: string
  children: MenuOptionsItem[]
}

/** 获取菜单树(无需认证) */
export function getMenuTree() {
  return http.get<ListItem[]>('/menu/tree')
}

/** 获取菜单列表(需认证) */
export function getMenuList() {
  return http.get<ListItem[]>('/menu/list')
}

/** 获取用于角色权限分配的菜单树 */
export function getMenuOptions() {
  return http.get<MenuOptionsItem[]>('/menu/tree')
}

/** 新增菜单 */
export function addMenu(data: { parentId: number, title: string, name?: string, path?: string, component?: string, icon?: string, menuType?: number, permission?: string, sort?: number, visible?: number, status?: number }) {
  return http.post('/menu', data)
}

/** 修改菜单 */
export function updateMenu(data: { id: number | string, parentId?: number, title?: string, name?: string, path?: string, component?: string, icon?: string, menuType?: number, permission?: string, sort?: number, visible?: number, status?: number }) {
  return http.request({ method: 'put', url: '/menu', data })
}

/** 删除菜单 */
export function deleteMenu(id: number | string) {
  return http.request({ method: 'delete', url: `/menu/${id}` })
}

/** 兼容旧版 baseAPI 接口 */
export const baseAPI = {
  getList(params?: any) {
    return getMenuList()
  },
  getDetail(params: { id: number | string }) {
    return http.get<ListItem>(`/menu/${params.id}`)
  },
  add(params: any) {
    return addMenu(params)
  },
  update(params: any) {
    return updateMenu(params)
  },
  delete(params: { ids: (string | number)[] }) {
    const promises = params.ids.map(id => deleteMenu(id))
    return Promise.all(promises).then(() => ({ code: 200, data: true, message: '删除成功', success: true })) as any
  }
}
