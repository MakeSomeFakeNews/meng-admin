import { get, post, put, del } from '@/utils/request'
import type { RoleInfo, RoleQueryParams, RoleSaveParams, RoleAssignMenuParams } from '@/types/role'
import type { PageResult } from '@/types/api'

/**
 * 分页查询角色列表
 */
export function getRolePage(params: RoleQueryParams): Promise<PageResult<RoleInfo>> {
  return get<PageResult<RoleInfo>>('/role/page', params as unknown as Record<string, unknown>)
}

/**
 * 获取所有角色（不分页，用于下拉选择）
 */
export function getAllRoles(): Promise<RoleInfo[]> {
  return get<RoleInfo[]>('/role/list')
}

/**
 * 获取角色详情
 */
export function getRoleById(id: number): Promise<RoleInfo> {
  return get<RoleInfo>(`/role/${id}`)
}

/**
 * 新增角色
 */
export function saveRole(data: RoleSaveParams): Promise<void> {
  return post<void>('/role', data)
}

/**
 * 编辑角色
 */
export function updateRole(data: RoleSaveParams): Promise<void> {
  return put<void>('/role', data)
}

/**
 * 删除角色
 */
export function deleteRole(id: number): Promise<void> {
  return del<void>(`/role/${id}`)
}

/**
 * 获取角色已分配的菜单ID列表
 */
export function getRoleMenuIds(roleId: number): Promise<number[]> {
  return get<number[]>(`/role/${roleId}/menus`)
}

/**
 * 分配角色菜单权限
 */
export function assignRoleMenus(data: RoleAssignMenuParams): Promise<void> {
  return put<void>('/role/assign-menus', data)
}
