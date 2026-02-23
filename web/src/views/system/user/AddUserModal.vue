<template>
  <a-modal v-model:visible="visible" :title="title" width="calc(100% - 20px)" :mask-closable="false"
    :modal-style="{ maxWidth: '600px' }" :body-style="{ maxHeight: '70vh' }" @before-ok="save" @close="close">
    <a-form ref="formRef" :model="form" :rules="rules" size="medium" auto-label-width>
      <a-row>
        <a-col v-bind="col2Props">
          <a-form-item label="用户名" field="username">
            <a-input v-model.trim="form.username" placeholder="请输入用户名" allow-clear :disabled="isEdit"
              :max-length="10"></a-input>
          </a-form-item>
        </a-col>
        <a-col v-bind="col2Props">
          <a-form-item label="昵称" field="nickname">
            <a-input v-model.trim="form.nickname" placeholder="请输入昵称" allow-clear :max-length="10"></a-input>
          </a-form-item>
        </a-col>
        <a-col v-bind="col2Props">
          <a-form-item label="手机号码" field="phone">
            <a-input v-model.trim="form.phone" placeholder="请输入手机号码" allow-clear :max-length="11"></a-input>
          </a-form-item>
        </a-col>
        <a-col v-bind="col2Props">
          <a-form-item label="邮箱" field="email">
            <a-input v-model.trim="form.email" placeholder="请输入邮箱" allow-clear :max-length="30"></a-input>
          </a-form-item>
        </a-col>
      </a-row>

      <a-form-item v-if="!isEdit" label="密码" field="password">
        <a-input-password v-model.trim="form.password" placeholder="请输入密码" allow-clear :max-length="20"></a-input-password>
      </a-form-item>

      <a-form-item label="角色" field="roleIds">
        <a-select v-model="form.roleIds" :options="roleOptions" placeholder="请选择所属角色" multiple allow-clear
          :allow-search="{ retainInputValue: true }" />
      </a-form-item>

      <a-form-item label="状态" field="status">
        <a-switch v-model="form.status" type="round" :checked-value="1" :unchecked-value="0" checked-text="正常"
          unchecked-text="禁用" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import type { ColProps, FormInstance } from '@arco-design/web-vue'
import { Message } from '@arco-design/web-vue'
import { addUser, getUserDetail, updateUser } from '@/apis/system/user'
import { useResetReactive } from '@/hooks'
import { useRole } from '@/hooks/app'
import * as Regexp from '@/utils/regexp'

const emit = defineEmits<{
  (e: 'save-success'): void
}>()

const { roleList, getRoleList } = useRole()
getRoleList()
const roleOptions = computed(() => roleList.value.map((i) => ({ label: i.roleName, value: i.id })))

const col2Props: ColProps = { xs: 24, sm: 24, md: 12, lg: 12, xl: 12, xxl: 12 }
const formRef = useTemplateRef('formRef')
const userId = ref('')
const isEdit = computed(() => !!userId.value)
const title = computed(() => (isEdit.value ? '编辑用户' : '新增用户'))
const visible = ref(false)

const [form, resetForm] = useResetReactive({
  id: '',
  username: '',
  nickname: '',
  phone: '',
  email: '',
  password: '',
  roleIds: [] as number[],
  status: 1 as number,
})

const rules: FormInstance['rules'] = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 2, max: 10, message: '长度在 2 - 10个字符' }
  ],
  nickname: [
    { required: true, message: '请输入昵称' },
    { min: 2, max: 10, message: '长度在 2 - 10个字符' }
  ],
  email: [{ match: Regexp.Email, message: '邮箱格式不正确' }],
  phone: [{ match: Regexp.Phone, message: '手机号格式不正确' }],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, max: 20, message: '长度在 6 - 20个字符' }
  ],
  status: [{ required: true, message: '请选择状态' }]
}

const add = () => {
  userId.value = ''
  visible.value = true
}

const edit = async (id: string) => {
  visible.value = true
  userId.value = id
  const res = await getUserDetail(id)
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
      await updateUser({ id: userId.value, nickname: form.nickname, email: form.email, phone: form.phone, status: form.status })
      Message.success('修改成功')
    } else {
      await addUser({ username: form.username, password: form.password, nickname: form.nickname, email: form.email, phone: form.phone, status: form.status, roleIds: form.roleIds })
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
