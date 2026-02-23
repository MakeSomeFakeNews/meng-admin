export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
}

export interface MenuTreeVO {
  id: number
  parentId: number
  title: string
  name: string
  path: string
  component: string
  icon: string
  menuType: number
  permission: string
  sort: number
  visible: number
  status: number
  children: MenuTreeVO[]
}

export interface LoginVO {
  tokenName: string
  tokenValue: string
  userInfo: UserInfo
  permissions: string[]
  roles: string[]
  menus: MenuTreeVO[]
}

// Keep Login as alias for backward compat
export type Login = LoginVO
