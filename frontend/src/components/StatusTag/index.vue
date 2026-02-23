<template>
  <a-tag :color="tagColor">{{ tagText }}</a-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  value: number
  activeText?: string
  inactiveText?: string
  type?: 'status' | 'result'
}>(), {
  activeText: '正常',
  inactiveText: '禁用',
  type: 'status'
})

const tagColor = computed(() => {
  if (props.type === 'result') {
    return props.value === 1 ? 'success' : 'error'
  }
  return props.value === 1 ? 'success' : 'default'
})

const tagText = computed(() => {
  return props.value === 1 ? props.activeText : props.inactiveText
})
</script>
