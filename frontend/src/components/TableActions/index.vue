<template>
  <a-space size="small">
    <template v-for="action in visibleActions" :key="action.label">
      <a-popconfirm
        v-if="action.confirm"
        :title="action.confirm"
        ok-text="确定"
        cancel-text="取消"
        @confirm="action.onClick(record)"
      >
        <a-button
          type="link"
          size="small"
          :danger="action.danger"
          :loading="action.loading"
        >
          {{ action.label }}
        </a-button>
      </a-popconfirm>
      <a-button
        v-else
        type="link"
        size="small"
        :danger="action.danger"
        :loading="action.loading"
        @click="action.onClick(record)"
      >
        {{ action.label }}
      </a-button>
    </template>
  </a-space>
</template>

<script setup lang="ts">
import { computed } from 'vue'

export interface TableAction {
  label: string
  onClick: (record: any) => void
  danger?: boolean
  confirm?: string
  loading?: boolean
  hidden?: boolean | ((record: any) => boolean)
}

const props = defineProps<{
  actions: TableAction[]
  record: any
}>()

const visibleActions = computed(() =>
  props.actions.filter(action => {
    if (action.hidden === undefined) return true
    if (typeof action.hidden === 'function') return !action.hidden(props.record)
    return !action.hidden
  })
)
</script>
