<template>
  <div>
    <a-card :bordered="false" style="margin-bottom: 16px; border-radius: 8px">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="角色名称">
          <a-input v-model:value="queryParams.roleName" placeholder="请输入角色名称" allow-clear />
        </a-form-item>
        <a-form-item label="角色编码">
          <a-input v-model:value="queryParams.roleCode" placeholder="请输入角色编码" allow-clear />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="queryParams.status" placeholder="请选择状态" allow-clear style="width: 120px">
            <a-select-option :value="1">正常</a-select-option>
            <a-select-option :value="0">禁用</a-select-option>
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
        <a-button type="primary" v-permission="'sys:role:add'" @click="openAddModal">
          <PlusOutlined /> 新增角色
        </a-button>
      </div>

      <a-table :columns="columns" :data-source="tableData" :loading="loading" :pagination="pagination" row-key="id" @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'error'">{{ record.status === 1 ? '正常' : '禁用' }}</a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" v-permission="'sys:role:update'" @click="openEditModal(record)">编辑</a-button>
              <a-button type="link" size="small" v-permission="'sys:role:assign-menu'" @click="openMenuModal(record)">分配权限</a-button>
              <a-popconfirm title="确定删除该角色吗？" v-permission="'sys:role:delete'" @confirm="handleDelete(record.id)">
                <a-button type="link" size="small" danger>删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑角色 Modal -->
    <a-modal v-model:open="modalVisible" :title="isEdit ? '编辑角色' : '新增角色'" :confirm-loading="submitLoading" @ok="handleSubmit" @cancel="resetForm" width="520px">
      <a-form :model="formData" :rules="formRules" ref="formRef" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="角色名称" name="roleName">
          <a-input v-model:value="formData.roleName" placeholder="请输入角色名称" />
        </a-form-item>
        <a-form-item label="角色编码" name="roleCode">
          <a-input v-model:value="formData.roleCode" placeholder="请输入角色编码（如 ADMIN）" :disabled="isEdit" />
        </a-form-item>
        <a-form-item label="排序" name="sort">
          <a-input-number v-model:value="formData.sort" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="formData.status">
            <a-radio :value="1">正常</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="备注" name="remark">
          <a-textarea v-model:value="formData.remark" :rows="3" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 分配权限 Modal（菜单树） -->
    <a-modal v-model:open="menuModalVisible" title="分配权限" :confirm-loading="submitLoading" @ok="handleAssignMenus" @cancel="menuModalVisible = false" width="480px">
      <a-tree
        v-model:checkedKeys="checkedMenuIds"
        :tree-data="menuTreeData"
        checkable
        :default-expand-all="true"
        :field-names="{ title: 'title', key: 'id', children: 'children' }"
      />
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule, TablePaginationConfig } from 'ant-design-vue/es'
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { getRolePage, saveRole, updateRole, deleteRole, getRoleMenuIds, assignRoleMenus } from '@/api/role'
import { getMenuTree } from '@/api/menu'
import type { RoleInfo, RoleQueryParams, RoleSaveParams } from '@/types/role'
import type { MenuItem } from '@/types/menu'

const queryParams = reactive<RoleQueryParams>({ roleName: '', roleCode: '', status: null, current: 1, size: 10 })
const loading = ref(false)
const tableData = ref<RoleInfo[]>([])
const pagination = reactive({ current: 1, pageSize: 10, total: 0, showSizeChanger: true })

const columns = [
  { title: '角色名称', dataIndex: 'roleName' },
  { title: '角色编码', dataIndex: 'roleCode' },
  { title: '排序', dataIndex: 'sort' },
  { title: '状态', key: 'status' },
  { title: '备注', dataIndex: 'remark' },
  { title: '创建时间', dataIndex: 'createTime' },
  { title: '操作', key: 'action', width: 220 }
]

async function loadData() {
  loading.value = true
  try {
    const res = await getRolePage({ ...queryParams, current: pagination.current, size: pagination.pageSize })
    tableData.value = res.records
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryParams.roleName = ''
  queryParams.roleCode = ''
  queryParams.status = null
  pagination.current = 1
  loadData()
}

function handleTableChange(pag: TablePaginationConfig) {
  pagination.current = pag.current || 1
  pagination.pageSize = pag.pageSize || 10
  loadData()
}

const modalVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive<RoleSaveParams>({ roleName: '', roleCode: '', sort: 0, status: 1, remark: '' })
const formRules: Record<string, Rule[]> = {
  roleName: [{ required: true, message: '请输入角色名称' }],
  roleCode: [{ required: true, message: '请输入角色编码' }]
}

function openAddModal() {
  isEdit.value = false
  modalVisible.value = true
  resetForm()
}

function openEditModal(record: RoleInfo) {
  isEdit.value = true
  modalVisible.value = true
  Object.assign(formData, { id: record.id, roleName: record.roleName, roleCode: record.roleCode, sort: record.sort, status: record.status, remark: record.remark || '' })
}

function resetForm() {
  Object.assign(formData, { id: undefined, roleName: '', roleCode: '', sort: 0, status: 1, remark: '' })
  formRef.value?.resetFields()
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateRole(formData)
      message.success('编辑成功')
    } else {
      await saveRole(formData)
      message.success('新增成功')
    }
    modalVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: number) {
  await deleteRole(id)
  message.success('删除成功')
  loadData()
}

// 分配权限菜单树
const menuModalVisible = ref(false)
const menuTreeData = ref<MenuItem[]>([])
const checkedMenuIds = ref<number[]>([])
const currentRoleId = ref<number>()

async function openMenuModal(record: RoleInfo) {
  currentRoleId.value = record.id
  menuModalVisible.value = true
  const [tree, roleMenuIds] = await Promise.all([getMenuTree(), getRoleMenuIds(record.id)])
  menuTreeData.value = tree
  checkedMenuIds.value = roleMenuIds
}

async function handleAssignMenus() {
  if (!currentRoleId.value) return
  submitLoading.value = true
  try {
    await assignRoleMenus({ roleId: currentRoleId.value, menuIds: checkedMenuIds.value })
    message.success('权限分配成功')
    menuModalVisible.value = false
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => loadData())
</script>
