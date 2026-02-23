<template>
  <a-card :bordered="false" class="search-card">
    <a-form layout="inline" v-bind="$attrs">
      <slot />
      <a-form-item>
        <a-space>
          <a-button type="primary" :loading="loading" @click="$emit('search')">
            <template #icon><SearchOutlined /></template>
            搜索
          </a-button>
          <a-button @click="$emit('reset')">
            <template #icon><ReloadOutlined /></template>
            重置
          </a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </a-card>
</template>

<script setup lang="ts">
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue'

withDefaults(defineProps<{
  loading?: boolean
}>(), {
  loading: false
})

defineEmits<{
  search: []
  reset: []
}>()
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}
</style>
