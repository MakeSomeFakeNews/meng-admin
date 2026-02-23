import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  /** 侧边栏是否折叠 */
  const collapsed = ref(false)

  /** 面包屑列表 */
  const breadcrumbs = ref<Array<{ title: string; path?: string }>>([])

  function toggleCollapsed(): void {
    collapsed.value = !collapsed.value
  }

  function setCollapsed(val: boolean): void {
    collapsed.value = val
  }

  function setBreadcrumbs(crumbs: Array<{ title: string; path?: string }>): void {
    breadcrumbs.value = crumbs
  }

  return { collapsed, breadcrumbs, toggleCollapsed, setCollapsed, setBreadcrumbs }
})
