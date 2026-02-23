/**
 * 用户信息（VO）
 */
export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  email: string
  phone: string
  status: number
  createTime: string
}

/**
 * 用户列表查询参数
 */
export interface UserQueryParams {
  username?: string
  nickname?: string
  status?: number | null
  current: number
  size: number
}

/**
 * 新增用户参数
 */
export interface UserSaveParams {
  username: string
  password: string
  nickname: string
  email?: string
  phone?: string
  status: number
  roleIds?: number[]
}

/**
 * 编辑用户参数
 */
export interface UserUpdateParams {
  id: number
  nickname: string
  email?: string
  phone?: string
  status: number
}

/**
 * 重置密码参数
 */
export interface ResetPasswordParams {
  userId: number
  newPassword: string
}
