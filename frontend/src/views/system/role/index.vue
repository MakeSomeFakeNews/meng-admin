<template>
  <div class="role-page">
    <!-- 搜索区域 -->
    <div class="search-card-wrapper">
      <a-card :bordered="false" class="search-card">
        <a-form layout="inline" :model="queryParams" class="search-form">
          <a-form-item label="角色名称">
            <a-input
              v-model:value="queryParams.roleName"
              placeholder="请输入角色名称"
              allow-clear
              style="width: 180px"
            >
              <template #prefix><SearchOutlined style="color: #bfbfbf" /></template>
            </a-input>
          </a-form-item>
          <a-form-item label="角色编码">
            <a-input
              v-model:value="queryParams.roleCode"
              placeholder="请输入角色编码"
              allow-clear
              style="width: 180px"
            >
              <template #prefix><TagOutlined style="color: #bfbfbf" /></template>
            </a-input>
          </a-form-item>
          <a-form-item label="状态">
            <a-select
              v-model:value="queryParams.status"
              placeholder="请选择状态"
              allow-clear
              style="width: 120px"
            >
              <a-select-option :value="1">正常</a-select-option>
              <a-select-option :value="0">禁用</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item>
            <a-space>
              <a-button type="primary" @click="loadData" :loading="loading">
                <template #icon><SearchOutlined /></template>
                搜索
              </a-button>
              <a-button @click="resetQuery">
                <template #icon><ReloadOutlined /></template>
                重置
              </a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </a-card>
    </div>

    <!-- 表格区域 -->
    <a-card :bordered="false" class="main-card">
      <!-- 操作栏 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <a-button type="primary" v-permission="'sys:role:add'" @click="openAddModal">
            <template #icon><PlusOutlined /></template>
            新增角色
          </a-button>
        </div>
        <div class="toolbar-right">
          <a-tooltip title="刷新">
            <a-button :loading="loading" @click="loadData">
              <template #icon><ReloadOutlined /></template>
            </a-button>
          </a-tooltip>
        </div>
      </div>

      <a-table
        :columns="columns"
        :data-source="tableData"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        class="role-table"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'roleName'">
            <div class="role-name-cell">
              <a-avatar
                size="small"
                :style="{ backgroundColor: getRoleColor(record.roleName), flexShrink: 0 }"
              >
                {{ record.roleName?.charAt(0) }}
              </a-avatar>
              <span class="role-name-text">{{ record.roleName }}</span>
            </div>
          </template>

          <template v-if="column.key === 'roleCode'">
            <a-tag color="blue" class="code-tag">{{ record.roleCode }}</a-tag>
          </template>

          <template v-if="column.key === 'sort'">
            <span class="sort-num">{{ record.sort }}</span>
          </template>

          <template v-if="column.key === 'status'">
            <a-badge
              :status="record.status === 1 ? 'success' : 'error'"
              :text="record.status === 1 ? '正常' : '禁用'"
            />
          </template>

          <template v-if="column.key === 'remark'">
            <a-tooltip v-if="record.remark" :title="record.remark">
              <span class="remark-text">{{ record.remark }}</span>
            </a-tooltip>
            <span v-else class="text-muted">-</span>
          </template>

          <template v-if="column.key === 'action'">
            <a-space size="small">
              <a-tooltip title="编辑角色">
                <a-button
                  type="link"
                  size="small"
                  v-permission="'sys:role:update'"
                  @click="openEditModal(record)"
                  class="action-btn edit-btn"
                >
                  <template #icon><EditOutlined /></template>
                  编辑
                </a-button>
              </a-tooltip>
              <a-tooltip title="分配菜单权限">
                <a-button
                  type="link"
                  size="small"
                  v-permission="'sys:role:assign-menu'"
                  @click="openMenuModal(record)"
                  class="action-btn perm-btn"
                >
                  <template #icon><SafetyCertificateOutlined /></template>
                  权限
                </a-button>
              </a-tooltip>
              <span v-permission="'sys:role:delete'">
                <a-popconfirm
                  title="确定删除该角色吗？"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="handleDelete(record.id)"
                >
                  <a-button
                    type="link"
                    size="small"
                    danger
                    class="action-btn"
                  >
                    <template #icon><DeleteOutlined /></template>
                    删除
                  </a-button>
                </a-popconfirm>
              </span>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑角色 Modal -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEdit ? '编辑角色' : '新增角色'"
      :confirm-loading="submitLoading"
      @ok="handleSubmit"
      @cancel="resetForm"
      width="520px"
    >
      <a-form
        :model="formData"
        :rules="formRules"
        ref="formRef"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
        style="margin-top: 16px"
      >
        <a-form-item label="角色名称" name="roleName">
          <a-input
            v-model:value="formData.roleName"
            placeholder="请输入角色名称"
          >
            <template #prefix><UserOutlined style="color: #bfbfbf" /></template>
          </a-input>
        </a-form-item>
        <a-form-item label="角色编码" name="roleCode">
          <a-input
            v-model:value="formData.roleCode"
            placeholder="如 ADMIN、EDITOR"
            :disabled="isEdit"
          >
            <template #prefix><TagOutlined style="color: #bfbfbf" /></template>
          </a-input>
        </a-form-item>
        <a-form-item label="排序" name="sort">
          <a-input-number
            v-model:value="formData.sort"
            :min="0"
            :max="9999"
            style="width: 100%"
            placeholder="数值越小越靠前"
          />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-switch
            :checked="formData.status === 1"
            @change="(val: boolean) => (formData.status = val ? 1 : 0)"
            checked-children="正常"
            un-checked-children="禁用"
          />
        </a-form-item>
        <a-form-item label="备注" name="remark">
          <a-textarea
            v-model:value="formData.remark"
            :rows="3"
            placeholder="请输入备注信息（选填）"
            :maxlength="200"
            show-count
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 分配权限 Modal -->
    <a-modal
      v-model:open="menuModalVisible"
      :title="`分配菜单权限 - ${currentRoleName}`"
      :confirm-loading="submitLoading"
      @ok="handleAssignMenus"
      @cancel="menuModalVisible = false"
      width="520px"
    >
      <!-- 权限操作栏 -->
      <div class="perm-toolbar">
        <a-space>
          <a-button size="small" @click="checkAll">全选</a-button>
          <a-button size="small" @click="uncheckAll">取消全选</a-button>
          <a-button size="small" @click="expandAllTree">展开全部</a-button>
          <a-button size="small" @click="collapseAllTree">折叠全部</a-button>
        </a-space>
        <span class="perm-count">已选 <b>{{ checkedMenuIds.length }}</b> 项</span>
      </div>

      <!-- 搜索 -->
      <a-input
        v-model:value="treeSearchText"
        placeholder="搜索菜单..."
        allow-clear
        style="margin-bottom: 12px"
      >
        <template #prefix><SearchOutlined style="color: #bfbfbf" /></template>
      </a-input>

      <div class="perm-tree-wrapper">
        <a-tree
          v-model:checkedKeys="checkedMenuIds"
          v-model:expandedKeys="treeExpandedKeys"
          :tree-data="filteredMenuTree"
          checkable
          :field-names="{ title: 'title', key: 'id', children: 'children' }"
          check-strictly
          class="perm-tree"
        />
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule, TablePaginationConfig } from 'ant-design-vue/es'
import {
  SearchOutlined, ReloadOutlined, PlusOutlined, EditOutlined, DeleteOutlined,
  TeamOutlined, TagOutlined, UserOutlined, SafetyCertificateOutlined
} from '@ant-design/icons-vue'
import { getRolePage, saveRole, updateRole, deleteRole, getRoleMenuIds, assignRoleMenus } from '@/api/role'
import { getMenuTree } from '@/api/menu'
import type { RoleInfo, RoleQueryParams, RoleSaveParams } from '@/types/role'
import type { MenuItem } from '@/types/menu'

const queryParams = reactive<RoleQueryParams>({ roleName: '', roleCode: '', status: null, current: 1, size: 10 })
const loading = ref(false)
const tableData = ref<RoleInfo[]>([])
const pagination = reactive({ current: 1, pageSize: 10, total: 0, showSizeChanger: true, showTotal: (total: number) => `共 ${total} 条` })

const columns = [
  { title: '角色名称', key: 'roleName', width: 160 },
  { title: '角色编码', key: 'roleCode', width: 140 },
  { title: '排序', key: 'sort', width: 80, align: 'center' as const },
  { title: '状态', key: 'status', width: 90 },
  { title: '备注', key: 'remark', ellipsis: true },
  { title: '创建时间', dataIndex: 'createTime', width: 170 },
  { title: '操作', key: 'action', width: 200, fixed: 'right' as const }
]

// 根据角色名生成颜色
function getRoleColor(name: string): string {
  const colors = ['#1677ff', '#52c41a', '#faad14', '#f5222d', '#722ed1', '#13c2c2', '#eb2f96', '#fa8c16']
  let hash = 0
  for (let i = 0; i < (name || '').length; i++) {
    hash = (name.charCodeAt(i) + ((hash << 5) - hash)) | 0
  }
  return colors[Math.abs(hash) % colors.length]
}

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

// 表单
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
  Object.assign(formData, {
    id: record.id,
    roleName: record.roleName,
    roleCode: record.roleCode,
    sort: record.sort,
    status: record.status,
    remark: record.remark || ''
  })
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

// 分配权限
const menuModalVisible = ref(false)
const allMenuTree = ref<MenuItem[]>([])
const checkedMenuIds = ref<number[]>([])
const currentRoleId = ref<number>()
const currentRoleName = ref('')
const treeSearchText = ref('')
const treeExpandedKeys = ref<number[]>([])

// 收集所有节点 id
function collectIds(data: MenuItem[]): number[] {
  const ids: number[] = []
  function walk(list: MenuItem[]) {
    list.forEach(item => {
      ids.push(item.id)
      if (item.children?.length) walk(item.children)
    })
  }
  walk(data)
  return ids
}

// 根据搜索词过滤树
const filteredMenuTree = computed(() => {
  if (!treeSearchText.value) return allMenuTree.value
  const keyword = treeSearchText.value.toLowerCase()
  function filterTree(list: MenuItem[]): MenuItem[] {
    const result: MenuItem[] = []
    list.forEach(item => {
      if (item.title.toLowerCase().includes(keyword)) {
        result.push(item)
      } else if (item.children?.length) {
        const children = filterTree(item.children)
        if (children.length) {
          result.push({ ...item, children })
        }
      }
    })
    return result
  }
  return filterTree(allMenuTree.value)
})

async function openMenuModal(record: RoleInfo) {
  currentRoleId.value = record.id
  currentRoleName.value = record.roleName
  treeSearchText.value = ''
  menuModalVisible.value = true
  const [tree, roleMenuIds] = await Promise.all([getMenuTree(), getRoleMenuIds(record.id)])
  allMenuTree.value = tree
  checkedMenuIds.value = roleMenuIds
  // 默认展开一级
  treeExpandedKeys.value = tree.map(item => item.id)
}

function checkAll() {
  checkedMenuIds.value = collectIds(allMenuTree.value)
}

function uncheckAll() {
  checkedMenuIds.value = []
}

function expandAllTree() {
  treeExpandedKeys.value = collectIds(allMenuTree.value)
}

function collapseAllTree() {
  treeExpandedKeys.value = []
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

<style scoped>
.role-page {
  padding: 0;
}

/* 搜索卡片 */
.search-card-wrapper {
  padding: 0;
  margin-bottom: 12px;
}

.search-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.search-form {
  gap: 8px;
  flex-wrap: wrap;
}

/* 主卡片 */
.main-card {
  margin: 0 0 16px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

/* 操作栏 */
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 表格 */
.role-table :deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  color: #595959;
}

.role-table :deep(.ant-table-row:hover > td) {
  background: #f0f7ff !important;
}

/* 角色名单元格 */
.role-name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.role-name-text {
  font-weight: 500;
  color: #262626;
}

/* 角色编码 */
.code-tag {
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 12px;
  border-radius: 4px;
}

/* 排序 */
.sort-num {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #f5f5f5;
  font-size: 12px;
  font-weight: 600;
  color: #595959;
}

/* 备注 */
.remark-text {
  display: inline-block;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #8c8c8c;
  font-size: 13px;
  cursor: default;
}

.text-muted {
  color: #bfbfbf;
}

/* 操作按钮 */
.action-btn {
  padding: 0 4px;
  height: auto;
  font-size: 12px;
}

.edit-btn {
  color: #1677ff;
}

.edit-btn:hover {
  color: #0958d9 !important;
}

.perm-btn {
  color: #52c41a;
}

.perm-btn:hover {
  color: #389e0d !important;
}

/* 权限树工具栏 */
.perm-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  padding: 8px 12px;
  background: #fafafa;
  border-radius: 8px;
}

.perm-count {
  font-size: 13px;
  color: #8c8c8c;
}

.perm-count b {
  color: #1677ff;
}

/* 权限树容器 */
.perm-tree-wrapper {
  max-height: 380px;
  overflow-y: auto;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 8px;
}

.perm-tree-wrapper::-webkit-scrollbar {
  width: 6px;
}

.perm-tree-wrapper::-webkit-scrollbar-track {
  background: #f5f5f5;
  border-radius: 3px;
}

.perm-tree-wrapper::-webkit-scrollbar-thumb {
  background: #d9d9d9;
  border-radius: 3px;
}

.perm-tree :deep(.ant-tree-node-content-wrapper:hover) {
  background: #e6f4ff;
}
</style>
