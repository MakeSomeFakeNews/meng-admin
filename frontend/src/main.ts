import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import './styles/global.css'
import App from './App.vue'
import router from './router'
import { permission } from './utils/permission'
import { StatusTag, SearchCard, TableActions, PageContainer } from './components'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(Antd)

// 全局自定义指令 v-permission
app.directive('permission', permission)

// 注册全局组件
app.component('StatusTag', StatusTag)
app.component('SearchCard', SearchCard)
app.component('TableActions', TableActions)
app.component('PageContainer', PageContainer)

app.mount('#app')
