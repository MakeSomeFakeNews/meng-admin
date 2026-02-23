<template>
  <div>
    <a-card :bordered="false" style="margin-bottom: 16px; border-radius: 8px">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="用户名">
          <a-input v-model:value="queryParams.username" placeholder="请输入用户名" allow-clear />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="queryParams.status" placeholder="请选择" allow-clear style="width: 100px">
            <a-select-option :value="1">成功</a-select-option>
            <a-select-option :value="0">失败</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="loadData" :loading="loading"><SearchOutlined /> 搜索</a-button>
            <a-button @click="resetQuery"><ReloadOutlined /> 重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card :bordered="false" style="border-radius: 8px">
      <div style="margin-bottom: 16px">
        <a-popconfirm title="确定清空所有登录日志吗？" @confirm="handleClear">
          <a-button danger><DeleteOutlined /> 清空</a-button>
        </a-popconfirm>
      </div>

      <a-table :columns="columns" :data-source="tableData" :loading="loading" :pagination="pagination" row-key="id" @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'error'">{{ record.status === 1 ? '成功' : '失败' }}</a-tag>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { TablePaginationConfig } from 'ant-design-vue/es'
import { SearchOutlined, ReloadOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { getLoginLogPage, clearLoginLog } from '@/api/log'
import type { LoginLog, LoginLogQueryParams } from '@/types/log'

const queryParams = reactive<LoginLogQueryParams>({ username: '', status: null, current: 1, size: 10 })
const loading = ref(false)
const tableData = ref<LoginLog[]>([])
const pagination = reactive({ current: 1, pageSize: 10, total: 0, showSizeChanger: true })

const columns = [
  { title: '用户名', dataIndex: 'username', width: 120 },
  { title: 'IP', dataIndex: 'ip', width: 130 },
  { title: '归属地', dataIndex: 'ipLocation', width: 140 },
  { title: '浏览器', dataIndex: 'browser', width: 120 },
  { title: '操作系统', dataIndex: 'os', width: 120 },
  { title: '状态', key: 'status', width: 80 },
  { title: '提示信息', dataIndex: 'message', width: 150 },
  { title: '登录时间', dataIndex: 'loginTime', width: 160 }
]

async function loadData() {
  loading.value = true
  try {
    const res = await getLoginLogPage({ ...queryParams, current: pagination.current, size: pagination.pageSize })
    tableData.value = res.records
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryParams.username = ''
  queryParams.status = null
  pagination.current = 1
  loadData()
}

function handleTableChange(pag: TablePaginationConfig) {
  pagination.current = pag.current || 1
  pagination.pageSize = pag.pageSize || 10
  loadData()
}

async function handleClear() {
  await clearLoginLog()
  message.success('已清空')
  loadData()
}

onMounted(() => loadData())
</script>
