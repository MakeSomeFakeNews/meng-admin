import { get, post, put, del } from '@/utils/request'
import type { MenuItem, MenuSaveParams } from '@/types/menu'

/**
 * 获取菜单树（全量）
 */
export function getMenuTree(): Promise<MenuItem[]> {
  return get<MenuItem[]>('/menu/tree')
}

/**
 * 获取菜单列表（树形，用于菜单管理页）
 */
export function getMenuList(): Promise<MenuItem[]> {
  return get<MenuItem[]>('/menu/list')
}

/**
 * 新增菜单
 */
export function saveMenu(data: MenuSaveParams): Promise<void> {
  return post<void>('/menu', data)
}

/**
 * 编辑菜单
 */
export function updateMenu(data: MenuSaveParams): Promise<void> {
  return put<void>('/menu', data)
}

/**
 * 删除菜单
 */
export function deleteMenu(id: number): Promise<void> {
  return del<void>(`/menu/${id}`)
}
