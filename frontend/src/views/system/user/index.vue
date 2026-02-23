<template>
  <PageContainer>
    <!-- 搜索区域 -->
    <template #search>
      <SearchCard :loading="loading" @search="handleSearch" @reset="handleReset">
        <a-form-item label="用户名">
          <a-input v-model:value="queryParams.username" placeholder="请输入用户名" allow-clear />
        </a-form-item>
        <a-form-item label="昵称">
          <a-input v-model:value="queryParams.nickname" placeholder="请输入昵称" allow-clear />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="queryParams.status" placeholder="请选择状态" allow-clear style="width: 120px">
            <a-select-option :value="1">正常</a-select-option>
            <a-select-option :value="0">禁用</a-select-option>
          </a-select>
        </a-form-item>
      </SearchCard>
    </template>

    <!-- 工具栏 -->
    <template #toolbar>
      <a-button type="primary" v-permission="'sys:user:add'" @click="openAddModal">
        <PlusOutlined /> 新增用户
      </a-button>
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
          <StatusTag :value="record.status" />
        </template>
        <template v-if="column.key === 'action'">
          <TableActions :actions="tableActions" :record="record" />
        </template>
      </template>
    </a-table>
  </PageContainer>

  <!-- 新增/编辑用户 Modal -->
  <a-modal
    v-model:open="modalVisible"
    :title="isEdit ? '编辑用户' : '新增用户'"
    :confirm-loading="submitLoading"
    @ok="handleSubmit"
    @cancel="resetForm"
    width="560px"
  >
    <a-form :model="formData" :rules="formRules" ref="formRef" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
      <a-form-item label="用户名" name="username" v-if="!isEdit">
        <a-input v-model:value="formData.username" placeholder="请输入用户名（3-32个字符）" />
      </a-form-item>
      <a-form-item label="密码" name="password" v-if="!isEdit">
        <a-input-password v-model:value="formData.password" placeholder="请输入密码（至少8位）" />
      </a-form-item>
      <a-form-item label="昵称" name="nickname">
        <a-input v-model:value="formData.nickname" placeholder="请输入昵称" />
      </a-form-item>
      <a-form-item label="邮箱" name="email">
        <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
      </a-form-item>
      <a-form-item label="手机号" name="phone">
        <a-input v-model:value="formData.phone" placeholder="请输入手机号" />
      </a-form-item>
      <a-form-item label="状态" name="status">
        <a-radio-group v-model:value="formData.status">
          <a-radio :value="1">正常</a-radio>
          <a-radio :value="0">禁用</a-radio>
        </a-radio-group>
      </a-form-item>
    </a-form>
  </a-modal>

  <!-- 分配角色 Modal -->
  <a-modal
    v-model:open="roleModalVisible"
    title="分配角色"
    :confirm-loading="submitLoading"
    @ok="handleAssignRoles"
    @cancel="roleModalVisible = false"
  >
    <a-checkbox-group v-model:value="selectedRoleIds" class="role-checkbox-group">
      <a-checkbox v-for="role in allRoles" :key="role.id" :value="role.id">
        {{ role.roleName }} ({{ role.roleCode }})
      </a-checkbox>
    </a-checkbox-group>
  </a-modal>

  <!-- 重置密码 Modal -->
  <a-modal
    v-model:open="resetPwdModalVisible"
    title="重置密码"
    :confirm-loading="submitLoading"
    @ok="handleResetPassword"
    @cancel="resetPwdModalVisible = false"
  >
    <a-form :model="resetPwdData" ref="resetPwdFormRef">
      <a-form-item
        label="新密码"
        name="newPassword"
        :rules="[{ required: true, message: '请输入新密码' }, { min: 8, message: '密码至少8位' }]"
      >
        <a-input-password v-model:value="resetPwdData.newPassword" placeholder="请输入新密码（至少8位）" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule, TablePaginationConfig } from 'ant-design-vue/es'
import { PlusOutlined } from '@ant-design/icons-vue'
import {
  getUserPage,
  saveUser,
  updateUser,
  deleteUser,
  getUserRoleIds,
  assignUserRoles,
  resetPassword
} from '@/api/user'
import { getAllRoles } from '@/api/role'
import type { UserInfo, UserQueryParams } from '@/types/user'
import type { RoleInfo } from '@/types/role'
import type { TableAction } from '@/components/TableActions/index.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 查询参数
const queryParams = reactive<UserQueryParams>({
  username: '',
  nickname: '',
  status: null,
  current: 1,
  size: 10
})

const loading = ref(false)
const tableData = ref<UserInfo[]>([])
const pagination = reactive({ current: 1, pageSize: 10, total: 0, showSizeChanger: true, showQuickJumper: true })

const columns = [
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '昵称', dataIndex: 'nickname', key: 'nickname' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '手机号', dataIndex: 'phone', key: 'phone' },
  { title: '状态', key: 'status' },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime' },
  { title: '操作', key: 'action', width: 280 }
]

// 操作列按钮配置
const tableActions: TableAction[] = [
  {
    label: '编辑',
    hidden: () => !authStore.hasPermission('sys:user:update'),
    onClick: (record: UserInfo) => openEditModal(record)
  },
  {
    label: '分配角色',
    hidden: () => !authStore.hasPermission('sys:user:assign-role'),
    onClick: (record: UserInfo) => openRoleModal(record)
  },
  {
    label: '重置密码',
    hidden: () => !authStore.hasPermission('sys:user:reset-pwd'),
    onClick: (record: UserInfo) => openResetPwdModal(record)
  },
  {
    label: '删除',
    danger: true,
    confirm: '确定删除该用户吗？',
    hidden: () => !authStore.hasPermission('sys:user:delete'),
    onClick: (record: UserInfo) => handleDelete(record.id)
  }
]

async function loadData() {
  loading.value = true
  try {
    const res = await getUserPage({
      ...queryParams,
      current: pagination.current,
      size: pagination.pageSize
    })
    tableData.value = res.records
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.current = 1
  loadData()
}

function handleReset() {
  queryParams.username = ''
  queryParams.nickname = ''
  queryParams.status = null
  pagination.current = 1
  loadData()
}

function handleTableChange(pag: TablePaginationConfig) {
  pagination.current = pag.current || 1
  pagination.pageSize = pag.pageSize || 10
  loadData()
}

// 新增/编辑 Modal
const modalVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive({
  id: undefined as number | undefined,
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  status: 1
})

const formRules: Record<string, Rule[]> = {
  username: [{ required: true, message: '请输入用户名' }, { min: 3, max: 32, message: '用户名3-32个字符' }],
  password: [{ required: true, message: '请输入密码' }, { min: 8, message: '密码至少8位' }],
  nickname: [{ required: true, message: '请输入昵称' }]
}

function openAddModal() {
  isEdit.value = false
  modalVisible.value = true
  resetForm()
}

function openEditModal(record: UserInfo) {
  isEdit.value = true
  modalVisible.value = true
  Object.assign(formData, {
    id: record.id,
    nickname: record.nickname,
    email: record.email || '',
    phone: record.phone || '',
    status: record.status
  })
}

function resetForm() {
  formData.id = undefined
  formData.username = ''
  formData.password = ''
  formData.nickname = ''
  formData.email = ''
  formData.phone = ''
  formData.status = 1
  formRef.value?.resetFields()
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (isEdit.value && formData.id) {
      await updateUser({ id: formData.id, nickname: formData.nickname, email: formData.email, phone: formData.phone, status: formData.status })
      message.success('编辑成功')
    } else {
      await saveUser({ username: formData.username, password: formData.password, nickname: formData.nickname, email: formData.email, phone: formData.phone, status: formData.status })
      message.success('新增成功')
    }
    modalVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: number) {
  await deleteUser(id)
  message.success('删除成功')
  loadData()
}

// 分配角色 Modal
const roleModalVisible = ref(false)
const allRoles = ref<RoleInfo[]>([])
const selectedRoleIds = ref<number[]>([])
const currentUserId = ref<number>()

async function openRoleModal(record: UserInfo) {
  currentUserId.value = record.id
  roleModalVisible.value = true
  const [roles, userRoleIds] = await Promise.all([
    getAllRoles(),
    getUserRoleIds(record.id)
  ])
  allRoles.value = roles
  selectedRoleIds.value = userRoleIds
}

async function handleAssignRoles() {
  if (!currentUserId.value) return
  submitLoading.value = true
  try {
    await assignUserRoles(currentUserId.value, selectedRoleIds.value)
    message.success('分配角色成功')
    roleModalVisible.value = false
  } finally {
    submitLoading.value = false
  }
}

// 重置密码 Modal
const resetPwdModalVisible = ref(false)
const resetPwdFormRef = ref<FormInstance>()
const resetPwdData = reactive({ userId: 0, newPassword: '' })

function openResetPwdModal(record: UserInfo) {
  resetPwdData.userId = record.id
  resetPwdData.newPassword = ''
  resetPwdModalVisible.value = true
}

async function handleResetPassword() {
  await resetPwdFormRef.value?.validate()
  submitLoading.value = true
  try {
    await resetPassword({ userId: resetPwdData.userId, newPassword: resetPwdData.newPassword })
    message.success('密码重置成功')
    resetPwdModalVisible.value = false
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.role-checkbox-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}
</style>
