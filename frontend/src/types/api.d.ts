/**
 * 通用API响应结构
 */
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
  timestamp: number
}

/**
 * 分页响应结构
 */
export interface PageResult<T> {
  total: number
  current: number
  size: number
  records: T[]
}

/**
 * 分页查询参数
 */
export interface PageParams {
  current: number
  size: number
}
