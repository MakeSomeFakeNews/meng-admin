<template>
  <PageContainer>
    <!-- 搜索区域 -->
    <template #search>
      <SearchCard :loading="loading" @search="loadData" @reset="resetQuery">
        <a-form-item label="模块">
          <a-input v-model:value="queryParams.module" placeholder="请输入模块名" allow-clear />
        </a-form-item>
        <a-form-item label="操作人">
          <a-input v-model:value="queryParams.operatorName" placeholder="请输入操作人" allow-clear />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="queryParams.status" placeholder="请选择" allow-clear style="width: 100px">
            <a-select-option :value="1">正常</a-select-option>
            <a-select-option :value="0">异常</a-select-option>
          </a-select>
        </a-form-item>
      </SearchCard>
    </template>

    <!-- 工具栏 -->
    <template #toolbar>
      <div>
        <a-popconfirm title="确定清空所有操作日志吗？" @confirm="handleClear">
          <a-button danger><DeleteOutlined /> 清空</a-button>
        </a-popconfirm>
      </div>
    </template>

    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="tableData"
      :loading="loading"
      :pagination="pagination"
      row-key="id"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <StatusTag :value="record.status" type="result" active-text="成功" inactive-text="失败" />
        </template>
        <template v-if="column.key === 'duration'">
          <span>{{ record.duration }}ms</span>
        </template>
        <template v-if="column.key === 'action'">
          <TableActions :actions="operateActions" :record="record" />
        </template>
      </template>
    </a-table>
  </PageContainer>

  <!-- 详情 Modal -->
  <a-modal v-model:open="detailVisible" title="操作日志详情" :footer="null" width="700px">
    <a-descriptions :column="2" bordered size="small" v-if="currentRecord">
      <a-descriptions-item label="模块">{{ currentRecord.module }}</a-descriptions-item>
      <a-descriptions-item label="操作">{{ currentRecord.action }}</a-descriptions-item>
      <a-descriptions-item label="操作人">{{ currentRecord.operatorName }}</a-descriptions-item>
      <a-descriptions-item label="IP">{{ currentRecord.ip }}</a-descriptions-item>
      <a-descriptions-item label="IP归属地">{{ currentRecord.ipLocation }}</a-descriptions-item>
      <a-descriptions-item label="耗时">{{ currentRecord.duration }}ms</a-descriptions-item>
      <a-descriptions-item label="请求URL" :span="2">{{ currentRecord.requestUrl }}</a-descriptions-item>
      <a-descriptions-item label="请求参数" :span="2">
        <pre class="detail-pre">{{ currentRecord.requestParams }}</pre>
      </a-descriptions-item>
      <a-descriptions-item label="响应结果" :span="2" v-if="currentRecord.status === 1">
        <pre class="detail-pre">{{ currentRecord.responseResult }}</pre>
      </a-descriptions-item>
      <a-descriptions-item label="错误信息" :span="2" v-if="currentRecord.status === 0">
        <span class="error-text">{{ currentRecord.errorMsg }}</span>
      </a-descriptions-item>
      <a-descriptions-item label="操作时间" :span="2">{{ currentRecord.createTime }}</a-descriptions-item>
    </a-descriptions>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { TablePaginationConfig } from 'ant-design-vue/es'
import { DeleteOutlined } from '@ant-design/icons-vue'
import { getOperateLogPage, clearOperateLog } from '@/api/log'
import type { OperateLog, OperateLogQueryParams } from '@/types/log'
import type { TableAction } from '@/components/TableActions/index.vue'

const queryParams = reactive<OperateLogQueryParams>({ module: '', operatorName: '', status: null, current: 1, size: 10 })
const loading = ref(false)
const tableData = ref<OperateLog[]>([])
const pagination = reactive({ current: 1, pageSize: 10, total: 0, showSizeChanger: true })

const columns = [
  { title: '模块', dataIndex: 'module', width: 100 },
  { title: '操作', dataIndex: 'action', width: 120 },
  { title: '操作人', dataIndex: 'operatorName', width: 100 },
  { title: 'IP', dataIndex: 'ip', width: 130 },
  { title: '归属地', dataIndex: 'ipLocation', width: 130 },
  { title: '状态', key: 'status', width: 80 },
  { title: '耗时', key: 'duration', width: 80 },
  { title: '操作时间', dataIndex: 'createTime', width: 160 },
  { title: '操作', key: 'action', width: 80 }
]

async function loadData() {
  loading.value = true
  try {
    const res = await getOperateLogPage({ ...queryParams, current: pagination.current, size: pagination.pageSize })
    tableData.value = res.records
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryParams.module = ''
  queryParams.operatorName = ''
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
  await clearOperateLog()
  message.success('已清空')
  loadData()
}

const detailVisible = ref(false)
const currentRecord = ref<OperateLog | null>(null)

function viewDetail(record: OperateLog) {
  currentRecord.value = record
  detailVisible.value = true
}

const operateActions: TableAction[] = [
  {
    label: '详情',
    onClick: (record) => viewDetail(record)
  }
]

onMounted(() => loadData())
</script>

<style scoped>
.detail-pre {
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 200px;
  overflow: auto;
}

.error-text {
  color: var(--error-color);
}
</style>
