/**
 * 角色信息（VO）
 */
export interface RoleInfo {
  id: number
  roleName: string
  roleCode: string
  sort: number
  status: number
  remark: string
  createTime: string
}

/**
 * 角色查询参数
 */
export interface RoleQueryParams {
  roleName?: string
  roleCode?: string
  status?: number | null
  current: number
  size: number
}

/**
 * 新增/编辑角色参数
 */
export interface RoleSaveParams {
  id?: number
  roleName: string
  roleCode: string
  sort: number
  status: number
  remark?: string
}

/**
 * 角色分配菜单参数
 */
export interface RoleAssignMenuParams {
  roleId: number
  menuIds: number[]
}
