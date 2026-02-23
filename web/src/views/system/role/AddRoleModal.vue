<template>
  <a-modal v-model:visible="visible" :title="title" width="calc(100% - 20px)" :mask-closable="false"
    :modal-style="{ maxWidth: '520px' }" @before-ok="save" @close="close">
    <a-form ref="formRef" :model="form" :rules="rules" size="medium" auto-label-width>
      <a-form-item label="角色名称" field="roleName">
        <a-input v-model.trim="form.roleName" placeholder="请输入角色名称" allow-clear :max-length="10"> </a-input>
      </a-form-item>
      <a-form-item label="角色编码" field="roleCode">
        <a-input v-model.trim="form.roleCode" placeholder="请输入角色编码" allow-clear :disabled="isEdit" :max-length="10">
        </a-input>
      </a-form-item>
      <a-form-item label="备注" field="remark">
        <a-textarea v-model.trim="form.remark" placeholder="请填写备注" :max-length="200" show-word-limit
          :auto-size="{ minRows: 3, maxRows: 5 }" />
      </a-form-item>
      <a-form-item label="状态" field="status">
        <a-switch v-model="form.status" type="round" :checked-value="1" :unchecked-value="0" checked-text="正常"
          unchecked-text="禁用" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import type { FormInstance } from '@arco-design/web-vue'
import { Message } from '@arco-design/web-vue'
import { addRole, updateRole } from '@/apis/system/role'
import { baseAPI } from '@/apis/system/role'
import { useResetReactive } from '@/hooks'

const emit = defineEmits<{
  (e: 'save-success'): void
}>()

const formRef = useTemplateRef('formRef')
const roleId = ref('')
const isEdit = computed(() => !!roleId.value)
const title = computed(() => (isEdit.value ? '编辑角色' : '新增角色'))
const visible = ref(false)

const [form, resetForm] = useResetReactive({
  roleName: '',
  roleCode: '',
  status: 1,
  remark: ''
})

const rules: FormInstance['rules'] = {
  roleName: [
    { required: true, message: '请输入角色名称' },
    { min: 2, max: 10, message: '长度在 2 - 10个字符' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码' },
    { match: /^[a-z]\w*$/i, message: '格式不对！只能英文开头，包含英文数字下划线' }
  ],
  status: [{ required: true }]
}

const add = () => {
  roleId.value = ''
  visible.value = true
}

const edit = async (id: string) => {
  visible.value = true
  roleId.value = id
  const res = await baseAPI.getDetail({ id })
  Object.assign(form, res.data)
}

const close = () => {
  formRef.value?.resetFields()
  resetForm()
}

const save = async () => {
  try {
    const valid = await formRef.value?.validate()
    if (valid) return false
    if (isEdit.value) {
      await updateRole({ id: roleId.value, ...form })
      Message.success('修改成功')
    } else {
      await addRole(form)
      Message.success('新增成功')
    }
    emit('save-success')
    return true
  } catch {
    return false
  }
}

defineExpose({ add, edit })
</script>
