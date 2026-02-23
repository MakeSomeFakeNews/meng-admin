import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface TabItem {
  path: string
  title: string
}

export const useTabsStore = defineStore('tabs', () => {
  const tabs = ref<TabItem[]>([])
  const activeKey = ref('')

  function addTab(path: string, title: string) {
    const existing = tabs.value.find(t => t.path === path)
    if (existing) {
      existing.title = title
    } else {
      tabs.value.push({ path, title })
    }
    activeKey.value = path
  }

  function closeTab(path: string): string | null {
    const idx = tabs.value.findIndex(t => t.path === path)
    if (idx === -1) return null
    tabs.value.splice(idx, 1)
    if (tabs.value.length === 0) {
      activeKey.value = ''
      return null
    }
    const newIdx = Math.max(0, idx - 1)
    const newPath = tabs.value[newIdx].path
    activeKey.value = newPath
    return newPath
  }

  function closeOthers(path: string) {
    tabs.value = tabs.value.filter(t => t.path === path)
    activeKey.value = path
  }

  function closeAll() {
    tabs.value = []
    activeKey.value = ''
  }

  function reset() {
    tabs.value = []
    activeKey.value = ''
  }

  return { tabs, activeKey, addTab, closeTab, closeOthers, closeAll, reset }
})
