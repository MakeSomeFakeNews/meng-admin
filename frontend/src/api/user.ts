import { get, post, put, del } from '@/utils/request'
import type { UserInfo, UserQueryParams, UserSaveParams, UserUpdateParams, ResetPasswordParams } from '@/types/user'
import type { PageResult } from '@/types/api'

/**
 * 分页查询用户列表
 */
export function getUserPage(params: UserQueryParams): Promise<PageResult<UserInfo>> {
  return get<PageResult<UserInfo>>('/user/page', params as unknown as Record<string, unknown>)
}

/**
 * 获取用户详情
 */
export function getUserById(id: number): Promise<UserInfo> {
  return get<UserInfo>(`/user/${id}`)
}

/**
 * 新增用户
 */
export function saveUser(data: UserSaveParams): Promise<void> {
  return post<void>('/user', data)
}

/**
 * 编辑用户
 */
export function updateUser(data: UserUpdateParams): Promise<void> {
  return put<void>('/user', data)
}

/**
 * 删除用户
 */
export function deleteUser(id: number): Promise<void> {
  return del<void>(`/user/${id}`)
}

/**
 * 重置密码
 */
export function resetPassword(data: ResetPasswordParams): Promise<void> {
  return put<void>('/user/reset-password', data)
}

/**
 * 获取用户已分配的角色ID列表
 */
export function getUserRoleIds(userId: number): Promise<number[]> {
  return get<number[]>(`/user/${userId}/roles`)
}

/**
 * 分配用户角色
 */
export function assignUserRoles(userId: number, roleIds: number[]): Promise<void> {
  return put<void>(`/user/${userId}/roles`, roleIds)
}

/**
 * 修改用户状态
 */
export function updateUserStatus(id: number, status: number): Promise<void> {
  return put<void>(`/user/${id}/status`, { status })
}
