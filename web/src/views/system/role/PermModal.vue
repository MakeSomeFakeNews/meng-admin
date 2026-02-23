<template>
  <a-modal v-model:visible="visible" :title="title" width="calc(100% - 20px)" :modal-style="{ maxWidth: '500px' }"
    :mask-closable="true" :body-style="{ maxHeight: '500px' }" @ok="save">
    <a-tree ref="treeRef" v-model:checked-keys="menuIds" size="mini" checkable :check-strictly="true" :data="treeData"
      :field-names="{ key: 'id' }"></a-tree>
  </a-modal>
</template>

<script lang="ts" setup>
import type { MenuOptionsItem } from '@/apis/system/menu'
import { Message } from '@arco-design/web-vue'
import { getMenuOptions } from '@/apis/system/menu'
import { assignRoleMenus, getRoleMenuIds } from '@/apis/system/role'

const treeRef = useTemplateRef('treeRef')
const treeData = ref<MenuOptionsItem[]>([])
const getTreeData = async () => {
  const res = await getMenuOptions()
  treeData.value = res.data
  nextTick(() => {
    treeRef.value?.expandAll()
  })
}
getTreeData()

const title = computed(() => `分配权限`)
const visible = ref(false)
const menuIds = ref<number[]>([])
const currentRoleId = ref<number | string>('')

const open = (data: { id: number | string, title: string }) => {
  menuIds.value = []
  currentRoleId.value = data.id
  visible.value = true
  getRoleMenuIds(data.id).then((res) => {
    menuIds.value = res.data
  })
}

const save = async () => {
  try {
    await assignRoleMenus({ roleId: currentRoleId.value, menuIds: menuIds.value })
    Message.success('权限分配成功')
    visible.value = false
  } catch (error) {
    Message.error('权限分配失败')
  }
}

defineExpose({ open })
</script>

<style lang="scss" scoped></style>
