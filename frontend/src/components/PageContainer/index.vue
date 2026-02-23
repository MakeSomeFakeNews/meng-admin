<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <div v-if="$slots.search" class="search-area">
      <slot name="search" />
    </div>
    <!-- 表格卡片 -->
    <a-card :bordered="false" class="table-card">
      <!-- 工具栏 -->
      <div v-if="$slots.toolbar" class="toolbar">
        <slot name="toolbar" />
      </div>
      <!-- 表格内容 -->
      <slot />
    </a-card>
  </div>
</template>

<script setup lang="ts">
// 纯容器组件，无需 script 逻辑
</script>

<style scoped>
.page-container {
  padding: 24px;
  min-height: 100%;
}

.search-area {
  margin-bottom: 16px;
}

.table-card {
  border-radius: 6px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
</style>
