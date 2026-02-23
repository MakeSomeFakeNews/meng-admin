/**
 * 菜单信息（VO）
 */
export interface MenuItem {
  id: number
  parentId: number
  title: string
  name: string
  path: string
  component: string
  icon: string
  menuType: number  // 0目录 1菜单 2按钮
  permission: string
  sort: number
  visible: number
  status: number
  children?: MenuItem[]
}

/**
 * 菜单树节点（用于前端动态路由）
 */
export interface MenuTreeNode {
  id: number
  parentId: number
  title: string
  name: string
  path: string
  component: string
  icon: string
  menuType: number
  permission: string
  children?: MenuTreeNode[]
}

/**
 * 新增/编辑菜单参数
 */
export interface MenuSaveParams {
  id?: number
  parentId: number
  title: string
  name?: string
  path?: string
  component?: string
  icon?: string
  menuType: number
  permission?: string
  sort: number
  visible: number
  status: number
}
