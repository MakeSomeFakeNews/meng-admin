/**
 * 操作日志
 */
export interface OperateLog {
  id: number
  module: string
  action: string
  requestUrl: string
  requestMethod: string
  requestParams: string
  responseResult: string
  status: number
  errorMsg: string
  operatorId: number
  operatorName: string
  ip: string
  ipLocation: string
  duration: number
  createTime: string
}

/**
 * 操作日志查询参数
 */
export interface OperateLogQueryParams {
  module?: string
  operatorName?: string
  status?: number | null
  startTime?: string
  endTime?: string
  current: number
  size: number
}

/**
 * 登录日志
 */
export interface LoginLog {
  id: number
  username: string
  ip: string
  ipLocation: string
  browser: string
  os: string
  status: number
  message: string
  loginTime: string
}

/**
 * 登录日志查询参数
 */
export interface LoginLogQueryParams {
  username?: string
  status?: number | null
  startTime?: string
  endTime?: string
  current: number
  size: number
}
