<template>
  <a-layout class="layout-container">
    <!-- 左侧菜单 -->
    <a-layout-sider
      v-model:collapsed="appStore.collapsed"
      :trigger="null"
      collapsible
      :width="220"
      class="sider"
    >
      <!-- Logo -->
      <div class="logo">
        <AppstoreOutlined v-if="appStore.collapsed" class="logo-icon" />
        <span v-else class="logo-title">后台管理系统</span>
      </div>

      <!-- 菜单 -->
      <a-menu
        v-model:selectedKeys="selectedKeys"
        v-model:openKeys="openKeys"
        theme="dark"
        mode="inline"
        :items="menuItems"
        class="sider-menu"
        :class="{ 'sider-menu--no-logo': !appStore.collapsed }"
        @click="handleMenuClick"
      />
    </a-layout-sider>

    <a-layout class="right-layout">
      <!-- 顶部 Header -->
      <a-layout-header class="header">
        <!-- 折叠按钮 -->
        <div class="trigger" @click="appStore.toggleCollapsed()">
          <MenuUnfoldOutlined v-if="appStore.collapsed" />
          <MenuFoldOutlined v-else />
        </div>

        <!-- 面包屑 -->
        <a-breadcrumb class="breadcrumb">
          <a-breadcrumb-item>首页</a-breadcrumb-item>
          <a-breadcrumb-item v-for="crumb in appStore.breadcrumbs" :key="crumb.title">
            {{ crumb.title }}
          </a-breadcrumb-item>
        </a-breadcrumb>

        <!-- 右侧用户信息 -->
        <div class="header-right">
          <a-dropdown placement="bottomRight">
            <div class="user-info">
              <a-avatar
                :style="{ backgroundColor: avatarBgColor, verticalAlign: 'middle' }"
                size="small"
              >
                {{ userInitial }}
              </a-avatar>
              <span class="username">{{ authStore.userInfo?.nickname || authStore.userInfo?.username }}</span>
              <DownOutlined class="down-icon" />
            </div>
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

      <!-- Tab 栏 -->
      <div class="tabs-bar">
        <div class="tabs-scroll">
          <a-tabs
            v-model:activeKey="tabsStore.activeKey"
            type="editable-card"
            hide-add
            size="small"
            class="page-tabs"
            @change="handleTabChange"
            @edit="handleTabEdit"
          >
            <a-tab-pane
              v-for="tab in tabsStore.tabs"
              :key="tab.path"
              :closable="tabsStore.tabs.length > 1"
            >
              <template #tab>
                <a-dropdown :trigger="['contextmenu']">
                  <span class="tab-title">{{ tab.title }}</span>
                  <template #overlay>
                    <a-menu>
                      <a-menu-item @click="contextClose(tab.path)">
                        <CloseOutlined /> 关闭
                      </a-menu-item>
                      <a-menu-item
                        :disabled="tabsStore.tabs.length <= 1"
                        @click="contextCloseOthers(tab.path)"
                      >
                        <MinusCircleOutlined /> 关闭其他
                      </a-menu-item>
                      <a-menu-divider />
                      <a-menu-item @click="contextCloseAll">
                        <CloseCircleOutlined /> 关闭所有
                      </a-menu-item>
                    </a-menu>
                  </template>
                </a-dropdown>
              </template>
            </a-tab-pane>
          </a-tabs>
        </div>

        <!-- Tab 操作下拉 -->
        <a-dropdown placement="bottomRight">
          <div class="tabs-ops-btn">
            <DownOutlined />
          </div>
          <template #overlay>
            <a-menu>
              <a-menu-item @click="opsCloseCurrentTab">
                <CloseOutlined /> 关闭当前
              </a-menu-item>
              <a-menu-item
                :disabled="tabsStore.tabs.length <= 1"
                @click="opsCloseOthers"
              >
                <MinusCircleOutlined /> 关闭其他
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item @click="opsCloseAll">
                <CloseCircleOutlined /> 关闭所有
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>

      <!-- 内容区 -->
      <a-layout-content class="content">
        <router-view v-slot="{ Component: RouteComponent, route: routeSlot }">
          <transition name="fade" mode="out-in">
            <component :is="RouteComponent" :key="routeSlot.path" />
          </transition>
        </router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { h, ref, computed, watch } from 'vue'
import type { Component } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Modal } from 'ant-design-vue'
import * as Icons from '@ant-design/icons-vue'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  LogoutOutlined,
  DownOutlined,
  AppstoreOutlined,
  CloseOutlined,
  MinusCircleOutlined,
  CloseCircleOutlined
} from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { useMenuStore } from '@/stores/menu'
import { useAppStore } from '@/stores/app'
import { useTabsStore } from '@/stores/tabs'
import type { MenuTreeNode } from '@/types/menu'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const menuStore = useMenuStore()
const appStore = useAppStore()
const tabsStore = useTabsStore()

const selectedKeys = ref<string[]>([])
const openKeys = ref<string[]>([])

const userInitial = computed(() => {
  const name = authStore.userInfo?.nickname || authStore.userInfo?.username || '?'
  return name.charAt(0).toUpperCase()
})

const avatarBgColor = computed(() => {
  const name = authStore.userInfo?.nickname || authStore.userInfo?.username || ''
  const colors = ['#1677ff', '#52c41a', '#faad14', '#f5222d', '#722ed1', '#13c2c2', '#eb2f96', '#fa8c16']
  let hash = 0
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash)
  return colors[Math.abs(hash) % colors.length]
})

// 监听路由变化：同步菜单选中、openKeys、面包屑、添加 Tab
watch(
  () => route.path,
  (path) => {
    selectedKeys.value = [path]
    if (!appStore.collapsed) {
      const keys: string[] = []
      findOpenKeys(menuStore.menuTree, path, keys)
      openKeys.value = keys
    }
    const crumbs = buildBreadcrumbs(menuStore.menuTree, path)
    appStore.setBreadcrumbs(crumbs)

    // 添加 Tab（获取标题：优先 route.meta.title，否则从菜单树查找）
    const title = (route.meta?.title as string) || findMenuTitle(menuStore.menuTree, path) || path
    tabsStore.addTab(path, title)
  },
  { immediate: true }
)

// 折叠时清空 openKeys，展开时恢复
watch(
  () => appStore.collapsed,
  (collapsed) => {
    if (collapsed) {
      openKeys.value = []
    } else {
      const keys: string[] = []
      findOpenKeys(menuStore.menuTree, route.path, keys)
      openKeys.value = keys
    }
  }
)

// 菜单 items 构建（支持动态图标）
const menuItems = computed(() => buildMenuItems(menuStore.menuTree))

function buildMenuItems(menus: MenuTreeNode[]): unknown[] {
  return menus
    .filter(menu => menu.menuType !== 2)
    .map(menu => {
      const iconComp = menu.icon ? (Icons as Record<string, unknown>)[menu.icon] : undefined
      const item: Record<string, unknown> = {
        key: menu.path || menu.name,
        label: menu.title,
        icon: iconComp ? () => h(iconComp as Component) : undefined
      }
      const children = (menu.children || []).filter((c: MenuTreeNode) => c.menuType !== 2)
      if (children.length > 0) item.children = buildMenuItems(children)
      return item
    })
}

function handleMenuClick({ key }: { key: string }) {
  router.push(key)
}

function findOpenKeys(menus: MenuTreeNode[], path: string, keys: string[]): boolean {
  for (const menu of menus) {
    if (menu.children?.some((c: MenuTreeNode) => c.path === path)) {
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
    if (menu.path === path) return [{ title: menu.title }]
    if (menu.children) {
      const sub = buildBreadcrumbs(menu.children, path)
      if (sub.length) return [{ title: menu.title }, ...sub]
    }
  }
  return []
}

function findMenuTitle(menus: MenuTreeNode[], path: string): string {
  for (const menu of menus) {
    if (menu.path === path) return menu.title
    if (menu.children) {
      const t = findMenuTitle(menu.children, path)
      if (t) return t
    }
  }
  return ''
}

// ========== Tab 操作 ==========

function handleTabChange(path: string) {
  router.push(path)
}

function handleTabEdit(targetKey: string | MouseEvent, action: 'add' | 'remove') {
  if (action === 'remove') {
    doCloseTab(targetKey as string)
  }
}

function doCloseTab(path: string) {
  const isActive = path === tabsStore.activeKey
  const newPath = tabsStore.closeTab(path)
  if (isActive) {
    router.push(newPath || '/dashboard')
  }
}

// 右键菜单操作
function contextClose(path: string) {
  doCloseTab(path)
}

function contextCloseOthers(path: string) {
  tabsStore.closeOthers(path)
  if (route.path !== path) router.push(path)
}

function contextCloseAll() {
  tabsStore.closeAll()
  router.push('/dashboard')
}

// 右上角下拉操作
function opsCloseCurrentTab() {
  doCloseTab(tabsStore.activeKey)
}

function opsCloseOthers() {
  tabsStore.closeOthers(tabsStore.activeKey)
}

function opsCloseAll() {
  tabsStore.closeAll()
  router.push('/dashboard')
}

async function handleLogout() {
  Modal.confirm({
    title: '确认退出',
    content: '确定要退出登录吗？',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      tabsStore.reset()
      authStore.logout()
    }
  })
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* ===== 右侧布局：flex 列，占满视口高度 ===== */
.right-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

/* ===== Sider ===== */
.sider {
  background: #001529 !important;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  z-index: 10;
}

/* ===== Logo ===== */
.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #002140;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  flex-shrink: 0;
}

.logo-icon {
  font-size: 20px;
  color: #1677ff;
}

.logo-title {
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 1px;
  white-space: nowrap;
}

/* ===== 菜单 ===== */
.sider-menu {
  border-right: none !important;
}

/* 展开时顶部加少量间距 */
.sider-menu--no-logo {
  padding-top: 8px;
}

/* ===== Header ===== */
.header {
  background: #fff !important;
  padding: 0 !important;
  display: flex;
  align-items: center;
  height: 56px;
  line-height: 56px;
  flex-shrink: 0;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 9;
}

.trigger {
  height: 56px;
  width: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  cursor: pointer;
  color: rgba(0, 0, 0, 0.65);
  transition: color 0.25s, background-color 0.25s;
  flex-shrink: 0;
}

.trigger:hover {
  color: #1677ff;
  background-color: rgba(22, 119, 255, 0.06);
}

.breadcrumb {
  flex: 1;
  margin-left: 8px;
}

.header-right {
  margin-right: 20px;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 6px;
  transition: background-color 0.2s;
  user-select: none;
}

.user-info:hover {
  background-color: rgba(0, 0, 0, 0.04);
}

.username {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.85);
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.down-icon {
  font-size: 11px;
  color: rgba(0, 0, 0, 0.45);
}

/* ===== Tab 栏 ===== */
.tabs-bar {
  display: flex;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
  padding-right: 4px;
  height: 40px;
}

.tabs-scroll {
  flex: 1;
  overflow: hidden;
  height: 40px;
}

/* a-tabs 样式覆盖 */
.page-tabs {
  height: 40px;
}

.page-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
  height: 40px;
}

.page-tabs :deep(.ant-tabs-nav::before) {
  border-bottom: none;
}

.page-tabs :deep(.ant-tabs-nav-wrap) {
  padding: 0 4px;
}

.page-tabs :deep(.ant-tabs-tab) {
  height: 30px !important;
  line-height: 30px !important;
  margin-top: 5px !important;
  padding: 0 10px !important;
  font-size: 13px;
  border-radius: 4px 4px 0 0 !important;
  background: #f5f5f5 !important;
  border-color: #e8e8e8 !important;
  transition: all 0.2s;
  user-select: none;
}

.page-tabs :deep(.ant-tabs-tab:hover) {
  background: #e6f4ff !important;
  border-color: #91caff !important;
}

.page-tabs :deep(.ant-tabs-tab.ant-tabs-tab-active) {
  background: #fff !important;
  border-color: #e8e8e8 !important;
  border-bottom-color: #fff !important;
}

.page-tabs :deep(.ant-tabs-tab.ant-tabs-tab-active .ant-tabs-tab-btn) {
  color: #1677ff;
  font-weight: 500;
}

.page-tabs :deep(.ant-tabs-content-holder) {
  display: none;
}

.page-tabs :deep(.ant-tabs-tab-remove) {
  font-size: 11px;
  opacity: 0.5;
  margin-left: 4px;
}

.page-tabs :deep(.ant-tabs-tab-remove:hover) {
  opacity: 1;
  color: #ff4d4f;
}

/* Tab 标题（右键触发区域） */
.tab-title {
  display: inline-block;
}

/* Tab 操作按钮（右上角下拉） */
.tabs-ops-btn {
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  margin: 0 4px;
  cursor: pointer;
  font-size: 11px;
  color: rgba(0, 0, 0, 0.45);
  background: #fff;
  flex-shrink: 0;
  transition: all 0.2s;
}

.tabs-ops-btn:hover {
  color: #1677ff;
  border-color: #1677ff;
  background: #e6f4ff;
}

/* ===== 内容区 ===== */
.content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  background: #f0f2f5;
  padding: 16px;
}

/* ===== 路由切换动画 ===== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
