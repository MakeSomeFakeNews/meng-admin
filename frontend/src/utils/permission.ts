import type { Directive, DirectiveBinding } from 'vue'
import { useAuthStore } from '@/stores/auth'

/**
 * 权限指令 v-permission
 * 用法：<a-button v-permission="'sys:user:add'">新增</a-button>
 * 无权限时自动从 DOM 移除该元素
 */
export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string>) {
    const { value } = binding
    if (!value) return

    const authStore = useAuthStore()
    if (!authStore.hasPermission(value)) {
      el.parentNode?.removeChild(el)
    }
  }
}
