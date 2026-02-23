/**
 * 业务枚举类型定义
 */

/** 通用状态枚举 */
export enum StatusEnum {
  DISABLED = 0,
  ENABLED = 1
}

/** 菜单类型枚举 */
export enum MenuTypeEnum {
  /** 目录 */
  CATALOG = 0,
  /** 菜单 */
  MENU = 1,
  /** 按钮/权限 */
  BUTTON = 2
}

/** 操作日志结果枚举 */
export enum OperateResultEnum {
  FAIL = 0,
  SUCCESS = 1
}

/** 请求方式枚举 */
export enum HttpMethodEnum {
  GET = 'GET',
  POST = 'POST',
  PUT = 'PUT',
  DELETE = 'DELETE',
  PATCH = 'PATCH'
}

/** API 响应码枚举 */
export enum ApiCodeEnum {
  SUCCESS = 200,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  SERVER_ERROR = 500
}

/** 状态选项（用于下拉选择） */
export const STATUS_OPTIONS = [
  { label: '正常', value: StatusEnum.ENABLED },
  { label: '禁用', value: StatusEnum.DISABLED }
] as const

/** 菜单类型选项 */
export const MENU_TYPE_OPTIONS = [
  { label: '目录', value: MenuTypeEnum.CATALOG },
  { label: '菜单', value: MenuTypeEnum.MENU },
  { label: '按钮', value: MenuTypeEnum.BUTTON }
] as const

/** 操作结果选项 */
export const OPERATE_RESULT_OPTIONS = [
  { label: '成功', value: OperateResultEnum.SUCCESS },
  { label: '失败', value: OperateResultEnum.FAIL }
] as const

/** 请求方式选项 */
export const HTTP_METHOD_OPTIONS = [
  { label: 'GET', value: HttpMethodEnum.GET },
  { label: 'POST', value: HttpMethodEnum.POST },
  { label: 'PUT', value: HttpMethodEnum.PUT },
  { label: 'DELETE', value: HttpMethodEnum.DELETE }
] as const
