<template>
  <div>
    <a-card :bordered="false" style="border-radius: 8px">
      <div style="margin-bottom: 16px">
        <a-button type="primary" v-permission="'sys:menu:add'" @click="openAddModal(0)">
          <PlusOutlined /> 新增菜单
        </a-button>
      </div>

      <a-table
        :columns="columns"
        :data-source="tableData"
        :loading="loading"
        :pagination="false"
        row-key="id"
        :default-expand-all-rows="true"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'menuType'">
            <a-tag :color="['blue', 'green', 'orange'][record.menuType]">
              {{ ['目录', '菜单', '按钮'][record.menuType] }}
            </a-tag>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'error'">{{ record.status === 1 ? '正常' : '禁用' }}</a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" v-permission="'sys:menu:add'" @click="openAddModal(record.id)" v-if="record.menuType !== 2">新增子项</a-button>
              <a-button type="link" size="small" v-permission="'sys:menu:update'" @click="openEditModal(record)">编辑</a-button>
              <a-popconfirm title="确定删除该菜单吗？" v-permission="'sys:menu:delete'" @confirm="handleDelete(record.id)">
                <a-button type="link" size="small" danger>删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 菜单表单 Modal -->
    <a-modal v-model:open="modalVisible" :title="isEdit ? '编辑菜单' : '新增菜单'" :confirm-loading="submitLoading" @ok="handleSubmit" @cancel="resetForm" width="600px">
      <a-form :model="formData" :rules="formRules" ref="formRef" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="菜单类型" name="menuType">
          <a-radio-group v-model:value="formData.menuType">
            <a-radio :value="0">目录</a-radio>
            <a-radio :value="1">菜单</a-radio>
            <a-radio :value="2">按钮</a-radio>
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
          />
        </a-form-item>
        <template v-if="formData.menuType !== 2">
          <a-form-item label="路由名称" name="name">
            <a-input v-model:value="formData.name" placeholder="请输入路由name（唯一）" />
          </a-form-item>
          <a-form-item label="路由路径" name="path">
            <a-input v-model:value="formData.path" placeholder="请输入路由路径（如 /system/user）" />
          </a-form-item>
        </template>
        <template v-if="formData.menuType === 1">
          <a-form-item label="组件路径" name="component">
            <a-input v-model:value="formData.component" placeholder="如 system/user/index（相对views目录，不含.vue）" />
          </a-form-item>
          <a-form-item label="图标" name="icon">
            <a-input v-model:value="formData.icon" placeholder="请输入图标名（@ant-design/icons-vue）" />
          </a-form-item>
        </template>
        <a-form-item label="权限标识" name="permission" v-if="formData.menuType !== 0">
          <a-input v-model:value="formData.permission" placeholder="如 sys:user:list" />
        </a-form-item>
        <a-form-item label="排序" name="sort">
          <a-input-number v-model:value="formData.sort" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="是否可见" name="visible" v-if="formData.menuType !== 2">
          <a-radio-group v-model:value="formData.visible">
            <a-radio :value="1">显示</a-radio>
            <a-radio :value="0">隐藏</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="formData.status">
            <a-radio :value="1">正常</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule } from 'ant-design-vue/es'
import { PlusOutlined } from '@ant-design/icons-vue'
import { getMenuList, saveMenu, updateMenu, deleteMenu } from '@/api/menu'
import type { MenuItem, MenuSaveParams } from '@/types/menu'

const loading = ref(false)
const tableData = ref<MenuItem[]>([])
const flatMenuTree = ref<MenuItem[]>([])

const columns = [
  { title: '菜单标题', dataIndex: 'title', key: 'title', width: 200 },
  { title: '类型', key: 'menuType', width: 80 },
  { title: '路由路径', dataIndex: 'path', key: 'path' },
  { title: '组件路径', dataIndex: 'component', key: 'component' },
  { title: '权限标识', dataIndex: 'permission', key: 'permission' },
  { title: '排序', dataIndex: 'sort', key: 'sort', width: 80 },
  { title: '状态', key: 'status', width: 80 },
  { title: '操作', key: 'action', width: 200 }
]

async function loadData() {
  loading.value = true
  try {
    const res = await getMenuList()
    tableData.value = res
    flatMenuTree.value = res
  } finally {
    loading.value = false
  }
}

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
  Object.assign(formData, { id: undefined, parentId: 0, title: '', name: '', path: '', component: '', icon: '', menuType: 0, permission: '', sort: 0, visible: 1, status: 1 })
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
