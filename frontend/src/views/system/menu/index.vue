<template>
  <div class="menu-page">
    <a-card :bordered="false" class="main-card">
      <!-- 操作栏 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <a-button type="primary" v-permission="'sys:menu:add'" @click="openAddModal(0)">
            <template #icon><PlusOutlined /></template>
            新增菜单
          </a-button>
        </div>
        <div class="toolbar-right">
          <a-space>
            <a-tooltip title="展开全部">
              <a-button @click="expandAll">
                <template #icon><FullscreenOutlined /></template>
                展开全部
              </a-button>
            </a-tooltip>
            <a-tooltip title="折叠全部">
              <a-button @click="collapseAll">
                <template #icon><FullscreenExitOutlined /></template>
                折叠全部
              </a-button>
            </a-tooltip>
            <a-tooltip title="刷新">
              <a-button :loading="loading" @click="loadData">
                <template #icon><ReloadOutlined /></template>
                刷新
              </a-button>
            </a-tooltip>
          </a-space>
        </div>
      </div>

      <!-- 表格 -->
      <a-table
        :columns="columns"
        :data-source="tableData"
        :loading="loading"
        :pagination="false"
        row-key="id"
        :expanded-row-keys="expandedKeys"
        @expand="onExpand"
        class="menu-table"
      >
        <template #bodyCell="{ column, record }">
          <!-- 菜单标题列 -->
          <template v-if="column.key === 'title'">
            <div class="menu-title-cell">
              <component
                v-if="record.icon && getIconComponent(record.icon)"
                :is="getIconComponent(record.icon)"
                class="menu-title-icon"
              />
              <span>{{ record.title }}</span>
            </div>
          </template>

          <!-- 图标列 -->
          <template v-if="column.key === 'icon'">
            <div v-if="record.icon" class="icon-preview-cell">
              <component
                v-if="getIconComponent(record.icon)"
                :is="getIconComponent(record.icon)"
                class="icon-preview"
              />
              <a-tooltip :title="record.icon">
                <span class="icon-name">{{ record.icon }}</span>
              </a-tooltip>
            </div>
            <span v-else class="text-muted">-</span>
          </template>

          <!-- 菜单类型列 -->
          <template v-if="column.key === 'menuType'">
            <a-tag :color="['blue', 'green', 'orange'][record.menuType]" class="type-tag">
              {{ ['目录', '菜单', '按钮'][record.menuType] }}
            </a-tag>
          </template>

          <!-- 路由路径列 -->
          <template v-if="column.key === 'path'">
            <a-tooltip v-if="record.path" :title="record.path">
              <span class="ellipsis-text">{{ record.path }}</span>
            </a-tooltip>
            <span v-else class="text-muted">-</span>
          </template>

          <!-- 组件路径列 -->
          <template v-if="column.key === 'component'">
            <a-tooltip v-if="record.component" :title="record.component">
              <span class="ellipsis-text code-text">{{ record.component }}</span>
            </a-tooltip>
            <span v-else class="text-muted">-</span>
          </template>

          <!-- 权限标识列 -->
          <template v-if="column.key === 'permission'">
            <a-tooltip v-if="record.permission" :title="record.permission">
              <a-tag color="purple" class="permission-tag">{{ record.permission }}</a-tag>
            </a-tooltip>
            <span v-else class="text-muted">-</span>
          </template>

          <!-- 状态列 -->
          <template v-if="column.key === 'status'">
            <a-badge
              :status="record.status === 1 ? 'success' : 'error'"
              :text="record.status === 1 ? '正常' : '禁用'"
            />
          </template>

          <!-- 操作列 -->
          <template v-if="column.key === 'action'">
            <a-space size="small">
              <a-tooltip title="新增子项" v-if="record.menuType !== 2">
                <a-button
                  type="link"
                  size="small"
                  v-permission="'sys:menu:add'"
                  @click="openAddModal(record.id)"
                  class="action-btn add-btn"
                >
                  <template #icon><PlusCircleOutlined /></template>
                  新增
                </a-button>
              </a-tooltip>
              <a-tooltip title="编辑菜单">
                <a-button
                  type="link"
                  size="small"
                  v-permission="'sys:menu:update'"
                  @click="openEditModal(record)"
                  class="action-btn edit-btn"
                >
                  <template #icon><EditOutlined /></template>
                  编辑
                </a-button>
              </a-tooltip>
              <span v-permission="'sys:menu:delete'">
                <a-popconfirm
                  title="确定要删除该菜单吗？"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="handleDelete(record.id)"
                >
                  <a-button
                    type="link"
                    size="small"
                    danger
                    class="action-btn delete-btn"
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

    <!-- 菜单表单 Modal -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEdit ? '编辑菜单' : '新增菜单'"
      :confirm-loading="submitLoading"
      @ok="handleSubmit"
      @cancel="resetForm"
      width="640px"
      class="menu-modal"
    >
      <a-form
        :model="formData"
        :rules="formRules"
        ref="formRef"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 17 }"
        class="menu-form"
      >
        <a-form-item label="菜单类型" name="menuType">
          <a-radio-group v-model:value="formData.menuType" button-style="solid">
            <a-radio-button :value="0">
              <FolderOutlined /> 目录
            </a-radio-button>
            <a-radio-button :value="1">
              <FileOutlined /> 菜单
            </a-radio-button>
            <a-radio-button :value="2">
              <ApiOutlined /> 按钮
            </a-radio-button>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="菜单标题" name="title">
          <a-input v-model:value="formData.title" placeholder="请输入菜单标题" />
        </a-form-item>

        <a-form-item label="上级菜单" name="parentId">
          <a-tree-select
            v-model:value="formData.parentId"
            :tree-data="flatMenuTree"
            :field-names="{ label: 'title', value: 'id', children: 'children' }"
            placeholder="请选择上级菜单（不选则为顶级）"
            allow-clear
            tree-default-expand-all
            style="width: 100%"
          />
        </a-form-item>

        <template v-if="formData.menuType !== 2">
          <a-form-item label="路由名称" name="name">
            <a-input v-model:value="formData.name" placeholder="请输入路由 name（唯一标识）" />
          </a-form-item>
          <a-form-item label="路由路径" name="path">
            <a-input v-model:value="formData.path" placeholder="如 /system/user" />
          </a-form-item>
        </template>

        <template v-if="formData.menuType === 1">
          <a-form-item label="组件路径" name="component">
            <a-input v-model:value="formData.component" placeholder="如 system/user/index（相对 views 目录，不含 .vue）" />
          </a-form-item>
        </template>

        <!-- 图标选择器（目录和菜单显示） -->
        <a-form-item label="菜单图标" name="icon" v-if="formData.menuType !== 2">
          <div class="icon-selector-wrapper">
            <div class="icon-input-row">
              <div class="icon-current-preview">
                <component
                  v-if="formData.icon && getIconComponent(formData.icon)"
                  :is="getIconComponent(formData.icon)"
                  class="icon-current"
                />
                <QuestionCircleOutlined v-else class="icon-current icon-placeholder" />
              </div>
              <a-input
                v-model:value="formData.icon"
                placeholder="请输入或选择图标名"
                style="flex: 1"
                allow-clear
              />
              <a-button type="default" @click="openIconPicker" class="icon-pick-btn">
                <template #icon><AppstoreOutlined /></template>
                选择图标
              </a-button>
            </div>
          </div>
        </a-form-item>

        <a-form-item label="权限标识" name="permission" v-if="formData.menuType !== 0">
          <a-input v-model:value="formData.permission" placeholder="如 sys:user:list" />
        </a-form-item>

        <a-form-item label="排序" name="sort">
          <a-input-number v-model:value="formData.sort" :min="0" style="width: 100%" placeholder="数值越小越靠前" />
        </a-form-item>

        <a-form-item label="是否可见" name="visible" v-if="formData.menuType !== 2">
          <a-switch
            :checked="formData.visible === 1"
            @change="(val: boolean) => (formData.visible = val ? 1 : 0)"
            checked-children="显示"
            un-checked-children="隐藏"
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
      </a-form>
    </a-modal>

    <!-- 图标选择器 Modal -->
    <a-modal
      v-model:open="iconPickerVisible"
      title="选择图标"
      :footer="null"
      width="720px"
      class="icon-picker-modal"
    >
      <div class="icon-picker-search">
        <a-input
          v-model:value="iconSearchText"
          placeholder="搜索图标名称..."
          allow-clear
          size="large"
        >
          <template #prefix><SearchOutlined /></template>
        </a-input>
      </div>
      <div class="icon-grid">
        <div
          v-for="iconName in filteredIcons"
          :key="iconName"
          class="icon-grid-item"
          :class="{ 'icon-grid-item-selected': formData.icon === iconName }"
          @click="selectIcon(iconName)"
        >
          <component :is="getIconComponent(iconName)" class="icon-grid-icon" />
          <span class="icon-grid-name">{{ iconName.replace('Outlined', '') }}</span>
        </div>
      </div>
      <div v-if="filteredIcons.length === 0" class="icon-empty">
        <a-empty description="未找到匹配的图标" />
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import type { Component } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule } from 'ant-design-vue/es'
import * as Icons from '@ant-design/icons-vue'
import {
  PlusOutlined,
  PlusCircleOutlined,
  EditOutlined,
  DeleteOutlined,
  ReloadOutlined,
  AppstoreOutlined,
  FullscreenOutlined,
  FullscreenExitOutlined,
  SearchOutlined,
  FolderOutlined,
  FileOutlined,
  ApiOutlined,
  QuestionCircleOutlined
} from '@ant-design/icons-vue'
import { getMenuList, saveMenu, updateMenu, deleteMenu } from '@/api/menu'
import type { MenuItem, MenuSaveParams } from '@/types/menu'

// 获取图标组件
function getIconComponent(iconName: string): Component | null {
  if (!iconName) return null
  return (Icons as Record<string, unknown>)[iconName] as Component | null
}

// 常用图标列表
const iconList = [
  'HomeOutlined', 'UserOutlined', 'SettingOutlined', 'DashboardOutlined',
  'TeamOutlined', 'SafetyCertificateOutlined', 'MenuOutlined', 'AppstoreOutlined',
  'BankOutlined', 'FileOutlined', 'FileTextOutlined', 'FolderOutlined',
  'LogoutOutlined', 'BarChartOutlined', 'PieChartOutlined', 'LineChartOutlined',
  'ShoppingOutlined', 'ShoppingCartOutlined', 'GiftOutlined', 'StarOutlined',
  'HeartOutlined', 'BellOutlined', 'MailOutlined', 'PhoneOutlined',
  'LockOutlined', 'UnlockOutlined', 'KeyOutlined', 'CloudOutlined',
  'DatabaseOutlined', 'ApiOutlined', 'CodeOutlined', 'RocketOutlined',
  'ThunderboltOutlined', 'FireOutlined', 'CrownOutlined', 'TrophyOutlined',
  'MobileOutlined', 'GlobalOutlined', 'CompassOutlined', 'EnvironmentOutlined',
  'CarOutlined', 'ShopOutlined', 'MoneyCollectOutlined', 'WalletOutlined',
  'CreditCardOutlined', 'AuditOutlined', 'ContainerOutlined', 'ProfileOutlined',
  'SolutionOutlined', 'ProjectOutlined', 'BuildOutlined', 'ToolOutlined',
  'ExperimentOutlined', 'FunctionOutlined', 'OrderedListOutlined', 'UnorderedListOutlined',
  'TableOutlined', 'FormOutlined', 'CalendarOutlined', 'ClockCircleOutlined',
  'ScheduleOutlined', 'NotificationOutlined', 'MessageOutlined', 'CommentOutlined',
  'LikeOutlined', 'SearchOutlined', 'FilterOutlined', 'ImportOutlined',
  'ExportOutlined', 'UploadOutlined', 'DownloadOutlined', 'PrinterOutlined',
  'CameraOutlined', 'PictureOutlined', 'AlertOutlined', 'InfoCircleOutlined',
  'CheckCircleOutlined', 'CloseCircleOutlined', 'WarningOutlined', 'PoweroffOutlined',
  'RadarChartOutlined', 'HeatMapOutlined', 'StockOutlined', 'FundOutlined'
]

// 图标搜索
const iconSearchText = ref('')
const filteredIcons = computed(() => {
  if (!iconSearchText.value) return iconList
  const keyword = iconSearchText.value.toLowerCase()
  return iconList.filter(name => name.toLowerCase().includes(keyword))
})

// 图标选择器
const iconPickerVisible = ref(false)
function openIconPicker() {
  iconSearchText.value = ''
  iconPickerVisible.value = true
}
function selectIcon(iconName: string) {
  formData.icon = iconName
  iconPickerVisible.value = false
}

// 表格展开控制
const expandedKeys = ref<number[]>([])

function collectAllKeys(data: MenuItem[]): number[] {
  const keys: number[] = []
  function walk(list: MenuItem[]) {
    list.forEach(item => {
      if (item.children && item.children.length > 0) {
        keys.push(item.id)
        walk(item.children)
      }
    })
  }
  walk(data)
  return keys
}

function expandAll() {
  expandedKeys.value = collectAllKeys(tableData.value)
}

function collapseAll() {
  expandedKeys.value = []
}

function onExpand(expanded: boolean, record: MenuItem) {
  if (expanded) {
    if (!expandedKeys.value.includes(record.id)) {
      expandedKeys.value = [...expandedKeys.value, record.id]
    }
  } else {
    expandedKeys.value = expandedKeys.value.filter(k => k !== record.id)
  }
}

// 表格数据
const loading = ref(false)
const tableData = ref<MenuItem[]>([])
const flatMenuTree = ref<MenuItem[]>([])

const columns = [
  { title: '菜单标题', key: 'title', width: 220 },
  { title: '图标', key: 'icon', width: 160 },
  { title: '类型', key: 'menuType', width: 80 },
  { title: '路由路径', key: 'path', dataIndex: 'path', width: 160, ellipsis: true },
  { title: '组件路径', key: 'component', dataIndex: 'component', width: 200, ellipsis: true },
  { title: '权限标识', key: 'permission', dataIndex: 'permission', width: 160 },
  { title: '排序', dataIndex: 'sort', key: 'sort', width: 70, align: 'center' as const },
  { title: '状态', key: 'status', width: 90 },
  { title: '操作', key: 'action', width: 210, fixed: 'right' as const }
]

async function loadData() {
  loading.value = true
  try {
    const res = await getMenuList()
    tableData.value = res
    flatMenuTree.value = res
    // 默认展开全部
    expandedKeys.value = collectAllKeys(res)
  } finally {
    loading.value = false
  }
}

// 表单
const modalVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive<MenuSaveParams>({
  parentId: 0,
  title: '',
  name: '',
  path: '',
  component: '',
  icon: '',
  menuType: 0,
  permission: '',
  sort: 0,
  visible: 1,
  status: 1
})

const formRules: Record<string, Rule[]> = {
  title: [{ required: true, message: '请输入菜单标题' }],
  menuType: [{ required: true, message: '请选择菜单类型' }]
}

function openAddModal(parentId: number) {
  isEdit.value = false
  modalVisible.value = true
  resetForm()
  formData.parentId = parentId
}

function openEditModal(record: MenuItem) {
  isEdit.value = true
  modalVisible.value = true
  Object.assign(formData, {
    id: record.id,
    parentId: record.parentId,
    title: record.title,
    name: record.name,
    path: record.path,
    component: record.component,
    icon: record.icon,
    menuType: record.menuType,
    permission: record.permission,
    sort: record.sort,
    visible: record.visible,
    status: record.status
  })
}

function resetForm() {
  Object.assign(formData, {
    id: undefined,
    parentId: 0,
    title: '',
    name: '',
    path: '',
    component: '',
    icon: '',
    menuType: 0,
    permission: '',
    sort: 0,
    visible: 1,
    status: 1
  })
  formRef.value?.resetFields()
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateMenu(formData)
      message.success('编辑成功')
    } else {
      await saveMenu(formData)
      message.success('新增成功')
    }
    modalVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: number) {
  await deleteMenu(id)
  message.success('删除成功')
  loadData()
}

onMounted(() => loadData())
</script>

<style scoped>
/* 页面布局 */
.menu-page {
  padding: 0;
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
.menu-table :deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  color: #595959;
}

.menu-table :deep(.ant-table-row:hover > td) {
  background: #f0f7ff !important;
}

/* 菜单标题单元格 */
.menu-title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.menu-title-icon {
  color: #1677ff;
  font-size: 15px;
}

/* 图标预览单元格 */
.icon-preview-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

.icon-preview {
  font-size: 16px;
  color: #1677ff;
}

.icon-name {
  font-size: 12px;
  color: #8c8c8c;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: default;
}

/* 类型标签 */
.type-tag {
  border-radius: 4px;
}

/* 文本省略 */
.ellipsis-text {
  display: inline-block;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: default;
}

.code-text {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace;
  font-size: 12px;
  color: #c41d7f;
  background: #fff0f6;
  padding: 1px 4px;
  border-radius: 3px;
}

.permission-tag {
  font-size: 11px;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
}

/* 空状态 */
.text-muted {
  color: #bfbfbf;
}

/* 操作按钮 */
.action-btn {
  padding: 0 4px;
  height: auto;
  font-size: 12px;
}

.add-btn {
  color: #52c41a;
}

.add-btn:hover {
  color: #389e0d !important;
}

.edit-btn {
  color: #1677ff;
}

.edit-btn:hover {
  color: #0958d9 !important;
}

/* 表单样式 */
.menu-form {
  padding: 8px 0;
}

/* 图标选择器 */
.icon-selector-wrapper {
  width: 100%;
}

.icon-input-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-current-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 32px;
  background: #f5f5f5;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  flex-shrink: 0;
}

.icon-current {
  font-size: 16px;
  color: #1677ff;
}

.icon-placeholder {
  color: #bfbfbf;
}

.icon-pick-btn {
  flex-shrink: 0;
  white-space: nowrap;
}

/* 图标选择器面板 */
.icon-picker-search {
  margin-bottom: 16px;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 8px;
  max-height: 420px;
  overflow-y: auto;
  padding: 4px;
}

.icon-grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px 4px 8px;
  border-radius: 8px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.2s ease;
  background: #fafafa;
  gap: 4px;
  min-height: 70px;
}

.icon-grid-item:hover {
  background: #e6f4ff;
  border-color: #91caff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(22, 119, 255, 0.15);
}

.icon-grid-item-selected {
  background: #e6f4ff !important;
  border-color: #1677ff !important;
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.2);
}

.icon-grid-icon {
  font-size: 20px;
  color: #595959;
  transition: color 0.2s;
}

.icon-grid-item:hover .icon-grid-icon {
  color: #1677ff;
}

.icon-grid-item-selected .icon-grid-icon {
  color: #1677ff !important;
}

.icon-grid-name {
  font-size: 10px;
  color: #8c8c8c;
  text-align: center;
  line-height: 1.2;
  word-break: break-all;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.icon-empty {
  padding: 40px 0;
}

/* 滚动条美化 */
.icon-grid::-webkit-scrollbar {
  width: 6px;
}

.icon-grid::-webkit-scrollbar-track {
  background: #f5f5f5;
  border-radius: 3px;
}

.icon-grid::-webkit-scrollbar-thumb {
  background: #d9d9d9;
  border-radius: 3px;
}

.icon-grid::-webkit-scrollbar-thumb:hover {
  background: #bfbfbf;
}
</style>
