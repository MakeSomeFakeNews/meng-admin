<template>
  <div style="max-width: 800px; margin: 0 auto">
    <a-card :bordered="false" style="border-radius: 8px">
      <a-tabs v-model:activeKey="activeTab">
        <!-- Tab 1: 基本信息 -->
        <a-tab-pane key="info" tab="基本信息">
          <a-row :gutter="48" style="padding: 24px 0">
            <!-- 头像 -->
            <a-col :span="8" style="text-align: center">
              <a-avatar :src="avatarPreview || authStore.userInfo?.avatar" :size="100" style="margin-bottom: 16px">
                {{ authStore.userInfo?.nickname?.charAt(0) }}
              </a-avatar>
              <br />
              <a-upload
                :show-upload-list="false"
                :before-upload="beforeUpload"
                :custom-request="handleAvatarUpload"
                accept="image/jpeg,image/png"
              >
                <a-button :loading="avatarUploading">更换头像</a-button>
              </a-upload>
              <p style="color: #999; font-size: 12px; margin-top: 8px">支持JPG/PNG，2MB以内</p>
            </a-col>
            <!-- 基本信息表单 -->
            <a-col :span="16">
              <a-form :model="infoForm" :rules="infoRules" ref="infoFormRef" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
                <a-form-item label="用户名">
                  <span>{{ authStore.userInfo?.username }}</span>
                </a-form-item>
                <a-form-item label="昵称" name="nickname">
                  <a-input v-model:value="infoForm.nickname" />
                </a-form-item>
                <a-form-item label="邮箱" name="email">
                  <a-input v-model:value="infoForm.email" />
                </a-form-item>
                <a-form-item label="手机号" name="phone">
                  <a-input v-model:value="infoForm.phone" />
                </a-form-item>
                <a-form-item :wrapper-col="{ offset: 6, span: 16 }">
                  <a-button type="primary" @click="handleUpdateInfo" :loading="infoLoading">保存修改</a-button>
                </a-form-item>
              </a-form>
            </a-col>
          </a-row>
        </a-tab-pane>

        <!-- Tab 2: 修改密码 -->
        <a-tab-pane key="password" tab="修改密码">
          <div style="max-width: 400px; margin: 24px auto">
            <a-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }">
              <a-form-item label="旧密码" name="oldPassword">
                <a-input-password v-model:value="pwdForm.oldPassword" />
              </a-form-item>
              <a-form-item label="新密码" name="newPassword">
                <a-input-password v-model:value="pwdForm.newPassword" />
                <div style="color: #999; font-size: 12px">至少8位，包含字母和数字</div>
              </a-form-item>
              <a-form-item label="确认密码" name="confirmPassword">
                <a-input-password v-model:value="pwdForm.confirmPassword" />
              </a-form-item>
              <a-form-item :wrapper-col="{ offset: 7, span: 17 }">
                <a-button type="primary" @click="handleUpdatePassword" :loading="pwdLoading">确认修改</a-button>
              </a-form-item>
            </a-form>
          </div>
        </a-tab-pane>

        <!-- Tab 3: 登录记录 -->
        <a-tab-pane key="logs" tab="登录记录">
          <a-table
            :columns="logColumns"
            :data-source="loginLogs"
            :loading="logsLoading"
            :pagination="logPagination"
            row-key="id"
            @change="handleLogTableChange"
            style="margin-top: 16px"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="record.status === 1 ? 'success' : 'error'">{{ record.status === 1 ? '成功' : '失败' }}</a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule, TablePaginationConfig, UploadRequestOption } from 'ant-design-vue/es'
import { useAuthStore } from '@/stores/auth'
import { getProfileInfo, updateProfileInfo, uploadAvatar, updatePassword, getProfileLoginLogs } from '@/api/profile'
import type { LoginLog } from '@/types/log'

const authStore = useAuthStore()
const activeTab = ref('info')

// --- 基本信息 ---
const infoFormRef = ref<FormInstance>()
const infoLoading = ref(false)
const avatarPreview = ref<string>('')
const avatarUploading = ref(false)

const infoForm = reactive({ nickname: '', email: '', phone: '' })
const infoRules: Record<string, Rule[]> = {
  nickname: [{ required: true, message: '昵称不能为空' }]
}

async function loadProfileInfo() {
  const res = await getProfileInfo()
  infoForm.nickname = res.nickname || ''
  infoForm.email = res.email || ''
  infoForm.phone = res.phone || ''
}

async function handleUpdateInfo() {
  await infoFormRef.value?.validate()
  infoLoading.value = true
  try {
    await updateProfileInfo(infoForm)
    message.success('修改成功')
    // 更新 store 中的用户信息
    await authStore.fetchUserInfo()
  } finally {
    infoLoading.value = false
  }
}

function beforeUpload(file: File): boolean {
  const isValidType = ['image/jpeg', 'image/png'].includes(file.type)
  const isValidSize = file.size <= 2 * 1024 * 1024
  if (!isValidType) { message.error('只支持 JPG/PNG 格式'); return false }
  if (!isValidSize) { message.error('图片大小不能超过2MB'); return false }
  // 预览
  const reader = new FileReader()
  reader.onload = e => { avatarPreview.value = e.target?.result as string }
  reader.readAsDataURL(file)
  return true
}

async function handleAvatarUpload(options: UploadRequestOption) {
  avatarUploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', options.file as File)
    await uploadAvatar(formData)
    message.success('头像上传成功')
    await authStore.fetchUserInfo()
  } catch {
    avatarPreview.value = ''
  } finally {
    avatarUploading.value = false
  }
}

// --- 修改密码 ---
const pwdFormRef = ref<FormInstance>()
const pwdLoading = ref(false)
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdRules: Record<string, Rule[]> = {
  oldPassword: [{ required: true, message: '请输入旧密码' }],
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 8, message: '密码至少8位' },
    {
      validator: (_rule, value) => {
        if (value && !/^(?=.*[A-Za-z])(?=.*\d).{8,}$/.test(value)) {
          return Promise.reject('密码需包含字母和数字')
        }
        return Promise.resolve()
      }
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码' },
    {
      validator: (_rule, value) => {
        if (value !== pwdForm.newPassword) return Promise.reject('两次密码不一致')
        return Promise.resolve()
      }
    }
  ]
}

async function handleUpdatePassword() {
  await pwdFormRef.value?.validate()
  pwdLoading.value = true
  try {
    await updatePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    message.success('密码修改成功，请重新登录')
    // 修改成功后退出登录
    setTimeout(async () => {
      await authStore.logout()
    }, 1500)
  } finally {
    pwdLoading.value = false
  }
}

// --- 登录记录 ---
const logsLoading = ref(false)
const loginLogs = ref<LoginLog[]>([])
const logPagination = reactive({ current: 1, pageSize: 20, total: 0, showSizeChanger: false })

const logColumns = [
  { title: '登录时间', dataIndex: 'loginTime', width: 170 },
  { title: 'IP地址', dataIndex: 'ip', width: 130 },
  { title: '登录地点', dataIndex: 'ipLocation', width: 150 },
  { title: '浏览器', dataIndex: 'browser', width: 120 },
  { title: '操作系统', dataIndex: 'os', width: 120 },
  { title: '结果', key: 'status', width: 80 }
]

async function loadLoginLogs() {
  logsLoading.value = true
  try {
    const res = await getProfileLoginLogs({ current: logPagination.current, size: logPagination.pageSize })
    loginLogs.value = res.records
    logPagination.total = res.total
  } finally {
    logsLoading.value = false
  }
}

function handleLogTableChange(pag: TablePaginationConfig) {
  logPagination.current = pag.current || 1
  loadLoginLogs()
}

watch(activeTab, (tab) => {
  if (tab === 'logs') loadLoginLogs()
})

onMounted(() => loadProfileInfo())
</script>
