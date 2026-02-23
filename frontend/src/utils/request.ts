import axios, { type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { message } from 'ant-design-vue'
import type { ApiResponse } from '@/types/api'

// 创建 Axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API as string,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器：自动附加 Authorization Token
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  (error: unknown) => {
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理响应和错误
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data
    // 正常业务响应
    if (res.code === 200) {
      return res.data as unknown as AxiosResponse<ApiResponse>
    }
    // 未认证（Token 失效/未登录）
    if (res.code === 401) {
      message.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      // 跳转登录页（避免循环导入 router，直接修改 location）
      window.location.href = '/login'
      return Promise.reject(new Error(res.message))
    }
    // 无权限
    if (res.code === 403) {
      message.error('权限不足，拒绝访问')
      return Promise.reject(new Error(res.message))
    }
    // 其他业务错误
    message.error(res.message || '操作失败')
    return Promise.reject(new Error(res.message))
  },
  (error: unknown) => {
    // 网络错误或超时
    if (axios.isAxiosError(error)) {
      if (error.code === 'ECONNABORTED') {
        message.error('请求超时，请稍后重试')
      } else if (error.response?.status === 500) {
        message.error('服务器内部错误')
      } else if (!error.response) {
        message.error('网络连接失败，请检查网络')
      } else {
        message.error(error.message || '请求失败')
      }
    }
    return Promise.reject(error)
  }
)

/**
 * 封装 GET 请求
 */
export function get<T>(url: string, params?: Record<string, unknown>): Promise<T> {
  return service.get(url, { params }) as unknown as Promise<T>
}

/**
 * 封装 POST 请求
 */
export function post<T>(url: string, data?: unknown): Promise<T> {
  return service.post(url, data) as unknown as Promise<T>
}

/**
 * 封装 PUT 请求
 */
export function put<T>(url: string, data?: unknown): Promise<T> {
  return service.put(url, data) as unknown as Promise<T>
}

/**
 * 封装 DELETE 请求
 */
export function del<T>(url: string, params?: Record<string, unknown>): Promise<T> {
  return service.delete(url, { params }) as unknown as Promise<T>
}

/**
 * 封装文件上传
 */
export function upload<T>(url: string, formData: FormData): Promise<T> {
  return service.put(url, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }) as unknown as Promise<T>
}

export default service
