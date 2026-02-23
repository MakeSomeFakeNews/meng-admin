<template>
  <a-layout class="layout-container">
    <!-- 侧边栏 -->
    <a-layout-sider
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      width="220"
      class="layout-sider"
    >
      <div class="logo">
        <span v-if="!collapsed">后台管理系统</span>
        <span v-else>管理</span>
      </div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        v-model:openKeys="openKeys"
        theme="dark"
        mode="inline"
        :items="menuItems"
        @click="handleMenuClick"
      />
    </a-layout-sider>

    <a-layout>
      <!-- 顶部导航 -->
      <a-layout-header class="layout-header">
        <div class="header-left">
          <menu-fold-outlined
            v-if="!collapsed"
            class="trigger"
            @click="collapsed = !collapsed"
          />
          <menu-unfold-outlined
            v-else
            class="trigger"
            @click="collapsed = !collapsed"
          />
        </div>
        <div class="header-right">
          <a-dropdown>
            <div class="user-info">
              <a-avatar :src="authStore.userInfo?.avatar" :size="32">
                {{ authStore.userInfo?.nickname?.charAt(0) }}
              </a-avatar>
              <span class="username">{{ authStore.userInfo?.nickname }}</span>
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile" @click="router.push('/profile')">
                  <UserOutlined />
                  个人中心
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>

      <!-- 内容区域 -->
      <a-layout-content class="layout-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import type { MenuProps } from 'ant-design-vue'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/auth'
import type { MenuTreeNode } from '@/types/menu'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const collapsed = ref(false)
const selectedKeys = ref<string[]>([])
const openKeys = ref<string[]>([])

// 根据当前路由设置选中菜单
watch(
  () => route.path,
  (path) => {
    selectedKeys.value = [path]
  },
  { immediate: true }
)

/**
 * 将后端菜单树转换为 ant-design-vue Menu items 格式
 */
function buildMenuItems(menus: MenuTreeNode[]): MenuProps['items'] {
  return menus
    .filter((m) => m.menuType !== 2) // 过滤按钮类型
    .map((menu) => {
      const item: NonNullable<MenuProps['items']>[number] = {
        key: menu.path || String(menu.id),
        label: menu.title,
        title: menu.title
      }
      if (menu.children && menu.children.length > 0) {
        const children = menu.children.filter((c) => c.menuType !== 2)
        if (children.length > 0) {
          return {
            ...item,
            children: buildMenuItems(children)
          }
        }
      }
      return item
    })
}

const menuItems = computed(() => buildMenuItems(authStore.menus))

function handleMenuClick({ key }: { key: string }): void {
  router.push(key)
}

async function handleLogout(): Promise<void> {
  Modal.confirm({
    title: '确认退出',
    content: '确定要退出登录吗？',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      await authStore.logout()
      await router.push('/login')
      message.success('已退出登录')
    }
  })
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.layout-sider {
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  z-index: 10;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.1);
  overflow: hidden;
  white-space: nowrap;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  height: 64px;
  line-height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
}

.trigger {
  font-size: 18px;
  cursor: pointer;
  transition: color 0.3s;
  padding: 0 8px;
}

.trigger:hover {
  color: #1677ff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 0 8px;
  border-radius: 4px;
  transition: background 0.3s;
}

.user-info:hover {
  background: rgba(0, 0, 0, 0.04);
}

.username {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.85);
}

.layout-content {
  margin: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  min-height: 280px;
  overflow: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
