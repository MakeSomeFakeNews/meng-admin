<template>
  <a-layout class="layout-container" style="min-height: 100vh">
    <!-- 左侧菜单 -->
    <a-layout-sider
      v-model:collapsed="appStore.collapsed"
      :trigger="null"
      collapsible
      :width="220"
      style="background: #001529"
    >
      <!-- Logo -->
      <div class="logo">
        <span v-if="!appStore.collapsed">后台管理系统</span>
        <span v-else>管理</span>
      </div>
      <!-- 菜单 -->
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
      <!-- 顶部 Header -->
      <a-layout-header style="background: #fff; padding: 0; display: flex; align-items: center; box-shadow: 0 1px 4px rgba(0,21,41,.08)">
        <!-- 折叠按钮 -->
        <div class="trigger" @click="appStore.toggleCollapsed()">
          <MenuUnfoldOutlined v-if="appStore.collapsed" />
          <MenuFoldOutlined v-else />
        </div>

        <!-- 面包屑 -->
        <a-breadcrumb style="margin-left: 12px; flex: 1">
          <a-breadcrumb-item>首页</a-breadcrumb-item>
          <a-breadcrumb-item v-for="crumb in appStore.breadcrumbs" :key="crumb.title">
            {{ crumb.title }}
          </a-breadcrumb-item>
        </a-breadcrumb>

        <!-- 右侧用户信息 -->
        <div style="margin-right: 24px">
          <a-dropdown>
            <a-space style="cursor: pointer">
              <a-avatar :src="authStore.userInfo?.avatar" size="small">
                {{ authStore.userInfo?.nickname?.charAt(0) }}
              </a-avatar>
              <span>{{ authStore.userInfo?.nickname || authStore.userInfo?.username }}</span>
            </a-space>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile" @click="router.push('/profile')">
                  <UserOutlined /> 个人中心
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout">
                  <LogoutOutlined /> 退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>

      <!-- 内容区 -->
      <a-layout-content style="margin: 16px; overflow: auto">
        <router-view v-slot="{ Component, route }">
          <transition name="fade" mode="out-in">
            <component :is="Component" :key="route.path" />
          </transition>
        </router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Modal } from 'ant-design-vue'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { useMenuStore } from '@/stores/menu'
import { useAppStore } from '@/stores/app'
import type { MenuTreeNode } from '@/types/menu'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const menuStore = useMenuStore()
const appStore = useAppStore()

const selectedKeys = ref<string[]>([])
const openKeys = ref<string[]>([])

// 根据当前路由设置选中的菜单
watch(
  () => route.path,
  (path) => {
    selectedKeys.value = [path]
    // 自动展开父级菜单
    const keys: string[] = []
    findOpenKeys(menuStore.menuTree, path, keys)
    if (!appStore.collapsed) {
      openKeys.value = keys
    }
    // 更新面包屑
    const crumbs = buildBreadcrumbs(menuStore.menuTree, path)
    appStore.setBreadcrumbs(crumbs)
  },
  { immediate: true }
)

// 根据菜单树构建 Ant Design Menu items
const menuItems = computed(() => buildMenuItems(menuStore.menuTree))

function buildMenuItems(menus: MenuTreeNode[]): unknown[] {
  return menus
    .filter(menu => menu.menuType !== 2 && menu.visible !== 0)
    .map(menu => {
      const item: Record<string, unknown> = {
        key: menu.path || menu.name,
        label: menu.title,
        icon: menu.icon ? () => null : undefined // 简化：图标暂不动态加载
      }
      if (menu.children && menu.children.filter(c => c.menuType !== 2).length > 0) {
        item.children = buildMenuItems(menu.children)
      }
      return item
    })
}

function handleMenuClick({ key }: { key: string }) {
  router.push(key)
}

function findOpenKeys(menus: MenuTreeNode[], path: string, keys: string[]): boolean {
  for (const menu of menus) {
    if (menu.children && menu.children.some(c => c.path === path)) {
      keys.push(menu.path || menu.name || '')
      return true
    }
    if (menu.children && findOpenKeys(menu.children, path, keys)) {
      keys.push(menu.path || menu.name || '')
      return true
    }
  }
  return false
}

function buildBreadcrumbs(menus: MenuTreeNode[], path: string): Array<{ title: string }> {
  for (const menu of menus) {
    if (menu.path === path) {
      return [{ title: menu.title }]
    }
    if (menu.children) {
      const sub = buildBreadcrumbs(menu.children, path)
      if (sub.length > 0) {
        return [{ title: menu.title }, ...sub]
      }
    }
  }
  return []
}

async function handleLogout() {
  Modal.confirm({
    title: '确认退出',
    content: '确定要退出登录吗？',
    onOk: () => authStore.logout()
  })
}
</script>

<style scoped>
.layout-container {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  background: #002140;
  overflow: hidden;
  white-space: nowrap;
}

.trigger {
  padding: 0 24px;
  font-size: 18px;
  cursor: pointer;
  transition: color 0.3s;
}

.trigger:hover {
  color: #1677ff;
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
