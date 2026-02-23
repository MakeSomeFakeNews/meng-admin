import type * as T from '@/apis/system/role'
import { ref } from 'vue'
import { getRoleList as fetchRoleList } from '@/apis/system/role'

/** 角色模块 */
export function useRole() {
  const loading = ref(false)
  const roleList = ref<T.ListItem[]>([])
  const total = ref(0)
  const getRoleList = async () => {
    try {
      loading.value = true
      const res = await fetchRoleList()
      roleList.value = (res.data as T.ListItem[]).filter((i) => i.status === 1)
      total.value = roleList.value.length
    } finally {
      loading.value = false
    }
  }
  return { roleList, getRoleList, loading, total }
}
