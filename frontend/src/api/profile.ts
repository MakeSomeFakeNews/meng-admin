import { get, put, upload } from '@/utils/request'
import type { UserInfo } from '@/types/user'
import type { LoginLog } from '@/types/log'
import type { PageResult } from '@/types/api'

/**
 * 获取当前用户基本信息
 */
export function getProfileInfo(): Promise<UserInfo> {
  return get<UserInfo>('/profile/info')
}

/**
 * 修改基本信息（昵称/邮箱/手机号）
 */
export function updateProfileInfo(data: { nickname: string; email?: string; phone?: string }): Promise<void> {
  return put<void>('/profile/info', data)
}

/**
 * 上传头像
 */
export function uploadAvatar(formData: FormData): Promise<string> {
  return upload<string>('/profile/avatar', formData)
}

/**
 * 修改密码
 */
export function updatePassword(data: { oldPassword: string; newPassword: string }): Promise<void> {
  return put<void>('/profile/password', data)
}

/**
 * 获取当前用户登录记录
 */
export function getProfileLoginLogs(params: { current: number; size: number }): Promise<PageResult<LoginLog>> {
  return get<PageResult<LoginLog>>('/profile/login-logs', params as unknown as Record<string, unknown>)
}
