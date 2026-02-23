import { get, del } from '@/utils/request'
import type { OperateLog, OperateLogQueryParams, LoginLog, LoginLogQueryParams } from '@/types/log'
import type { PageResult } from '@/types/api'

/**
 * 分页查询操作日志
 */
export function getOperateLogPage(params: OperateLogQueryParams): Promise<PageResult<OperateLog>> {
  return get<PageResult<OperateLog>>('/log/operate/page', params as unknown as Record<string, unknown>)
}

/**
 * 删除操作日志
 */
export function deleteOperateLog(ids: number[]): Promise<void> {
  return del<void>('/log/operate', { ids: ids.join(',') })
}

/**
 * 清空操作日志
 */
export function clearOperateLog(): Promise<void> {
  return del<void>('/log/operate/clear')
}

/**
 * 分页查询登录日志
 */
export function getLoginLogPage(params: LoginLogQueryParams): Promise<PageResult<LoginLog>> {
  return get<PageResult<LoginLog>>('/log/login/page', params as unknown as Record<string, unknown>)
}

/**
 * 删除登录日志
 */
export function deleteLoginLog(ids: number[]): Promise<void> {
  return del<void>('/log/login', { ids: ids.join(',') })
}

/**
 * 清空登录日志
 */
export function clearLoginLog(): Promise<void> {
  return del<void>('/log/login/clear')
}
