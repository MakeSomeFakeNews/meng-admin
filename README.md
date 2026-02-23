# Meng Admin

基于 Spring Boot 4 + Sa-Token + MyBatis Plus + Vue 3 的全栈后台权限管理脚手架

---

## 项目简介

Meng Admin 是一套开箱即用的全栈后台权限管理脚手架，基于 RBAC（基于角色的访问控制）模型设计，覆盖用户管理、角色管理、菜单权限、操作审计等核心后台管理场景。

前端采用 Vue 3 + Ant Design Vue 实现动态路由与按钮级权限控制，后端采用 Spring Boot 4 + Sa-Token 提供认证授权能力，MyBatis Plus 驱动数据访问层。项目结构清晰、代码规范统一，可作为中后台系统的快速起步基础。

---

## 功能特性

### 权限体系

- **RBAC 模型**：用户 → 角色 → 菜单/权限，三层关联，灵活可扩展
- **动态路由**：登录后从服务端加载菜单，前端动态注册路由，无需手动维护路由表
- **按钮级权限**：`v-permission` 指令控制页面操作按钮的显隐，权限粒度精确到接口
- **Sa-Token 认证**：Token 存储于 Redis，支持动态续签，禁止同账号多端同时登录

### 系统管理

- **用户管理**：用户增删改查、状态管理、角色分配、密码重置
- **角色管理**：角色增删改查、菜单权限树分配
- **菜单管理**：无限层级菜单树、目录/菜单/按钮三种类型、图标选择器
- **个人中心**：头像上传、基本信息修改、密码修改、个人登录记录查看

### 审计日志

- **操作日志**：AOP 自动采集，记录操作人、接口、参数、耗时、真实 IP 及归属地
- **登录日志**：记录每次登录的账号、IP、归属地、浏览器/系统信息及登录结果

### 工程质量

- 统一响应体 `R<T>`，统一异常处理 `GlobalExceptionHandler`
- MyBatis Plus 雪花 ID、逻辑删除、审计字段自动填充
- 敏感字段（密码）响应脱敏，密码参数日志自动替换为 `***`
- 操作日志异步写库（`@Async`），不影响主链路性能
- IP 归属地离线解析（ip2region），无需外部网络依赖

---

## 技术栈

### 后端

| 类别 | 技术 | 版本 |
|------|------|------|
| 核心框架 | Spring Boot | 4.0.3 |
| ORM | MyBatis Plus | 3.5.15 |
| 认证授权 | Sa-Token | 1.44.0 |
| 缓存 | Spring Data Redis | Boot 管理 |
| 数据库 | MySQL | 8.x |
| 构建工具 | Gradle (Groovy DSL) | 8.x |
| 运行环境 | Java | 17 |
| 工具库 | Hutool | 5.8.38 |
| 对象映射 | MapStruct | 1.6.3 |
| IP 解析 | ip2region | 2.7.0 |
| 代码简化 | Lombok | Boot 管理 |

### 前端

| 类别 | 技术 | 版本 |
|------|------|------|
| 构建工具 | Vite | 6.x |
| 核心框架 | Vue 3 (Composition API) | 3.x |
| 开发语言 | TypeScript | 5.x |
| UI 组件库 | Ant Design Vue | 4.x |
| 状态管理 | Pinia | 2.x |
| 路由 | Vue Router | 4.x |
| HTTP 客户端 | Axios | 1.x |
| 图标 | @ant-design/icons-vue | — |
| 样式 | Less + CSS Variables | — |
| 代码规范 | ESLint + Prettier | — |

---

## 项目结构

```
meng-admin/
├── backend/                          # 后端（Spring Boot）
│   └── src/main/
│       ├── java/me/dqq/admin/
│       │   ├── AdminApplication.java
│       │   ├── common/               # 公共基础层
│       │   │   ├── annotation/       # 自定义注解（@Log 等）
│       │   │   ├── aspect/           # AOP 切面（操作日志）
│       │   │   ├── config/           # 配置类（Redis / Sa-Token / MybatisPlus）
│       │   │   ├── constant/         # 常量（Redis Key 等）
│       │   │   ├── enums/            # 枚举
│       │   │   ├── exception/        # 全局异常处理
│       │   │   ├── handler/          # MyBatis Plus 自动填充
│       │   │   ├── result/           # 统一响应体 R<T>、PageResult
│       │   │   └── utils/            # 工具类（IpUtil 等）
│       │   └── module/               # 业务模块层
│       │       ├── auth/             # 登录 / 登出 / 用户信息
│       │       ├── user/             # 用户管理
│       │       ├── role/             # 角色管理
│       │       ├── menu/             # 菜单管理
│       │       ├── profile/          # 个人中心
│       │       └── log/              # 操作日志 / 登录日志
│       └── resources/
│           ├── application.yml
│           ├── application-dev.yml
│           ├── application-prod.yml
│           ├── ip2region/            # IP 离线数据库
│           └── mapper/               # MyBatis XML 映射
│
└── frontend/                         # 前端（Vite + Vue 3）
    └── src/
        ├── api/                      # 接口层（按模块）
        ├── layout/                   # 主布局（侧边栏 + Header）
        ├── router/                   # 路由（静态 + 动态生成）
        ├── stores/                   # Pinia 状态（auth / menu / app）
        ├── utils/                    # 工具（request.ts / permission.ts）
        ├── types/                    # TypeScript 类型声明
        ├── components/               # 全局公共组件
        └── views/                    # 页面
            ├── login/                # 登录页
            ├── dashboard/            # 首页
            ├── system/               # 系统管理（用户/角色/菜单）
            ├── log/                  # 日志（操作日志/登录日志）
            └── profile/              # 个人中心
```

---

## 数据库设计

项目共 7 张核心表，均使用雪花算法 ID，包含逻辑删除和审计字段。

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户表（账号、密码、头像、状态） |
| `sys_role` | 角色表（角色名、角色编码、状态） |
| `sys_menu` | 菜单权限表（树形结构、目录/菜单/按钮） |
| `sys_user_role` | 用户角色关联表 |
| `sys_role_menu` | 角色菜单关联表 |
| `sys_log_operate` | 操作日志（IP、归属地、耗时、操作人） |
| `sys_log_login` | 登录日志（IP、归属地、浏览器、系统） |

---

## 快速开始

### 环境要求

| 环境 | 版本要求 |
|------|---------|
| JDK | 17+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| Node.js | 18+ |
| Gradle | 8.x（或使用项目内 `./gradlew`）|

### 1. 克隆项目

```bash
git clone https://github.com/your-username/meng-admin.git
cd meng-admin
```

### 2. 初始化数据库

```bash
mysql -u root -p < backend/sql/init.sql
```

### 3. 启动后端

```bash
cd backend

# 复制配置模板，按本地环境填写 MySQL / Redis 连接信息
cp src/main/resources/application-dev.yml.example \
   src/main/resources/application-dev.yml

./gradlew bootRun
# 服务默认运行在 http://localhost:8080
```

### 4. 启动前端

```bash
cd frontend

npm install
npm run dev
# 页面默认运行在 http://localhost:3000
```

### 5. 默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| `admin` | `Admin@123` | 超级管理员 |

---

## 认证流程

```
用户登录
  │
  ▼
POST /auth/login
  │  校验账号密码（BCrypt）
  │  检查用户状态
  │  StpUtil.login(userId)
  │  权限信息写入 Redis
  │  记录登录日志（含 IP 归属地）
  │
  ▼
返回 Token + 用户信息 + 权限列表 + 菜单树
  │
  ▼
前端存储 Token → 动态注册路由 → 进入系统

─────────────────────────────────────────

后续每次请求
  │
  ▼
请求头携带 Authorization: <token>
  │
  ▼
Sa-Token 拦截器验证 Token
  │  Token 有效且在 Redis 中存在
  │  30 分钟内有操作则自动续签
  │
  ▼
StpInterfaceImpl 从 Redis 加载权限列表
  │
  ▼
@SaCheckPermission 校验接口权限
```

---

## 接口文档

### 认证

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/login` | 账号密码登录 |
| POST | `/auth/logout` | 登出 |
| GET | `/auth/info` | 获取当前用户信息、权限列表、菜单树 |

### 用户管理

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|---------|------|
| GET | `/sys/user/page` | `sys:user:list` | 分页查询 |
| POST | `/sys/user` | `sys:user:add` | 新增用户 |
| PUT | `/sys/user/{id}` | `sys:user:edit` | 修改用户 |
| DELETE | `/sys/user/{id}` | `sys:user:delete` | 删除用户 |
| PUT | `/sys/user/{id}/status` | `sys:user:edit` | 修改状态 |
| PUT | `/sys/user/{id}/role` | `sys:user:edit` | 分配角色 |
| PUT | `/sys/user/{id}/password` | `sys:user:edit` | 重置密码 |

### 角色管理

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|---------|------|
| GET | `/sys/role/list` | `sys:role:list` | 查询角色列表 |
| POST | `/sys/role` | `sys:role:add` | 新增角色 |
| PUT | `/sys/role/{id}` | `sys:role:edit` | 修改角色 |
| DELETE | `/sys/role/{id}` | `sys:role:delete` | 删除角色 |
| PUT | `/sys/role/{id}/menu` | `sys:role:edit` | 分配菜单权限 |

### 菜单管理

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|---------|------|
| GET | `/sys/menu/tree` | `sys:menu:list` | 查询菜单树 |
| POST | `/sys/menu` | `sys:menu:add` | 新增菜单 |
| PUT | `/sys/menu/{id}` | `sys:menu:edit` | 修改菜单 |
| DELETE | `/sys/menu/{id}` | `sys:menu:delete` | 删除菜单 |

### 个人中心

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/profile/info` | 获取个人信息 |
| PUT | `/profile/info` | 修改个人信息 |
| PUT | `/profile/avatar` | 上传头像 |
| PUT | `/profile/password` | 修改密码 |
| GET | `/profile/login-logs` | 个人登录记录 |

### 日志查询

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|---------|------|
| GET | `/sys/log/operate/page` | `sys:log:list` | 操作日志分页 |
| GET | `/sys/log/login/page` | `sys:log:list` | 登录日志分页 |

---

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1700000000000
}
```

| 状态码 | 含义 |
|--------|------|
| `200` | 请求成功 |
| `400` | 参数校验失败 |
| `401` | 未登录 / Token 失效 |
| `403` | 无访问权限 |
| `500` | 服务器内部错误 |

---

## 配置说明

`application-dev.yml` 和 `application-prod.yml` 均已加入 `.gitignore`，含敏感信息请勿提交至版本库。可参考以下关键配置项：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/meng_admin?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379

sa-token:
  timeout: 86400        # Token 有效期（秒）
  active-timeout: 1800  # 动态续签阈值（秒）
  is-concurrent: false  # 禁止多端同时登录
```

---

## 开发规范

### 后端

- 所有接口挂载 `@SaCheckPermission` 注解控制权限
- 需要记录操作日志的接口挂载 `@Log(module = "xx", action = "xx")`
- 业务异常统一抛出 `BusinessException`，由 `GlobalExceptionHandler` 捕获
- Entity 字段全部添加中文注释，VO 中 `password` 字段加 `@JsonIgnore`
- Service 层使用接口 + 实现类分离，禁止在 Controller 直接操作 Mapper

### 前端

- 所有操作按钮加 `v-permission="'权限标识'"` 控制显隐
- 异步请求期间展示 loading 状态（表格 `:loading`，按钮 `:loading`）
- Modal 关闭时调用 `formRef.value.resetFields()` 重置表单
- 接口统一在 `src/api/` 目录按模块维护，禁止在组件内直接调用 `axios`
- 禁止使用 `any` 类型，不确定类型使用 `unknown`

---

## 更新日志

### v1.0.0

- 完成 RBAC 权限体系（用户 / 角色 / 菜单）
- Sa-Token 认证，Redis 存储，动态续签
- AOP 操作日志，异步写库，IP 归属地离线解析
- 动态路由，按钮级权限控制
- 个人中心（基本信息 / 密码修改 / 登录记录）
- Vue 3 + Ant Design Vue 4 前端工程

---

## 许可证

本项目使用 [MIT](LICENSE) 许可证开源。