# 全栈后台权限管理脚手架 — AI 完整开发提示词

> 本提示词面向 Claude、GPT-4、Gemini 等 AI 编程助手，建议**分模块多轮对话**使用，每轮聚焦一个模块以获得最完整的代码输出。

---

## 一、项目概述

请帮我开发一套**完整可交付、开箱即用**的全栈后台权限管理脚手架（RBAC），包含前后端完整代码。

**目标**：代码可直接运行，无占位方法（`// TODO`），含完整注释，具备生产可用的基础架构。

---

## 二、技术栈

### 后端

| 类别 | 技术 & 版本 |
|------|------------|
| 框架 | Spring Boot 4.0.3 |
| ORM | MyBatis Plus 3.5.15（Spring Boot 4 专用 starter）|
| 认证授权 | Sa-Token 1.44.0 |
| 缓存 | Spring Data Redis |
| 数据库 | MySQL 8.x |
| 构建工具 | Gradle 8.x (Groovy DSL) |
| Java 版本 | Java 17 |
| 辅助 | Lombok、Spring Validation、Hutool、MapStruct |

**build.gradle 依赖清单（锁定版本，请完整生成此文件）：**

```groovy
dependencies {
    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'

    // MyBatis Plus（Spring Boot 4 专用，注意 starter 名称为 boot4）
    implementation 'com.baomidou:mybatis-plus-spring-boot4-starter:3.5.15'

    // Sa-Token（Spring Boot 通用版，兼容 Boot 4）
    // 注意：使用 sa-token-spring-boot-starter（不带版本号数字后缀），版本 1.44.0
    implementation 'cn.dev33:sa-token-spring-boot-starter:1.44.0'
    // Sa-Token Redis 持久化（jackson 序列化）
    implementation 'cn.dev33:sa-token-redis-jackson:1.44.0'
    // Redis 连接池（sa-token-redis-jackson 依赖）
    implementation 'org.apache.commons:commons-pool2'

    // 数据库驱动
    runtimeOnly 'com.mysql:mysql-connector-j'

    // 工具库
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    implementation 'cn.hutool:hutool-all:5.8.38'
    implementation 'org.mapstruct:mapstruct:1.6.3'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.6.3'
    // IP 归属地离线解析（不依赖外网）
    implementation 'org.lionsoul:ip2region:2.7.0'

    // 测试
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

> ⚠️ **兼容性说明（AI 必读）**：
> - `mybatis-plus-spring-boot4-starter` 是 MyBatis Plus 为 Spring Boot 4.x 提供的专用 starter，与 `boot3-starter` 不可混用
> - `sa-token-spring-boot-starter`（1.44.0）已原生支持 Spring Boot 4.x，无需使用带版本数字的 starter 变体
> - 如某依赖与 Spring Boot 4.x 的 Jakarta EE 命名空间（`jakarta.*`）存在冲突，请主动说明并给出修复方案，不得静默忽略

---

### 前端

| 类别 | 技术 & 版本 |
|------|------------|
| 构建工具 | Vite 6.x |
| 框架 | Vue 3.x（Composition API + `<script setup>`）|
| 语言 | TypeScript 5.x |
| UI 组件库 | Ant Design Vue 4.x |
| 状态管理 | Pinia 2.x |
| 路由 | Vue Router 4.x |
| HTTP 客户端 | Axios 1.x（封装拦截器）|
| 图标 | @ant-design/icons-vue |
| 样式 | CSS Variables + Less |
| 代码规范 | ESLint + Prettier |

---

## 三、数据库设计

请根据以下表结构说明，**自行设计并生成完整的 MySQL 建表 SQL**（包括建库语句、字符集、索引、注释）。

**所有表的公共约束：**
- 主键：`BIGINT` 类型，雪花算法生成，非自增
- 逻辑删除：`deleted TINYINT NOT NULL DEFAULT 0`（1=已删除）
- 审计字段：`create_by`、`update_by`（BIGINT）、`create_time`、`update_time`（DATETIME，自动维护）
- 字符集：`utf8mb4`，排序规则：`utf8mb4_unicode_ci`

**表清单及关键字段说明：**

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `sys_user` | 用户表 | username(唯一)、password(BCrypt)、nickname、avatar、email、phone、status |
| `sys_role` | 角色表 | role_name、role_code(唯一)、sort、status、remark |
| `sys_menu` | 菜单/权限表 | parent_id、title、name(路由name)、path、component、icon、menu_type(0目录/1菜单/2按钮)、permission(如`sys:user:list`)、sort、visible、status |
| `sys_user_role` | 用户角色关联 | user_id、role_id |
| `sys_role_menu` | 角色菜单关联 | role_id、menu_id |
| `sys_log_operate` | 操作日志 | module、action、method、request_url、request_method、request_params、response_result、status、error_msg、operator_id、operator_name、**ip**、**ip_location**、duration、create_time |
| `sys_log_login` | 登录日志 | username、**ip**、**ip_location**、browser、os、status(1成功/0失败)、message、login_time |

> `ip_location` 字段存储 IP 归属地（如"广东省深圳市"），由后端调用 Hutool 的 `IPUtil` 或离线 IP 库解析，**不依赖外部网络接口**。

---

## 四、后端项目结构

```
src/main/
├── java/me/dqq/admin/
│   ├── AdminApplication.java
│   ├── common/
│   │   ├── annotation/
│   │   │   ├── Log.java                  # 操作日志注解
│   │   │   └── RequiresPermission.java   # 权限注解（Sa-Token已内置，此处可扩展）
│   │   ├── aspect/
│   │   │   └── LogAspect.java            # 操作日志 AOP
│   │   ├── config/
│   │   │   ├── MybatisPlusConfig.java    # 分页插件、雪花ID、自动填充
│   │   │   ├── RedisConfig.java          # Redis 序列化配置
│   │   │   ├── SaTokenConfig.java        # Sa-Token 权限接口实现
│   │   │   └── WebMvcConfig.java         # 跨域、拦截器配置
│   │   ├── constant/
│   │   │   ├── RedisKeyConst.java        # Redis Key 常量
│   │   │   └── SystemConst.java          # 系统常量
│   │   ├── enums/
│   │   │   ├── MenuTypeEnum.java
│   │   │   └── StatusEnum.java
│   │   ├── exception/
│   │   │   ├── BusinessException.java    # 业务异常
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── handler/
│   │   │   └── MetaObjectHandlerImpl.java # MyBatis Plus 自动填充
│   │   └── result/
│   │       ├── R.java                    # 统一响应体
│   │       ├── PageResult.java           # 分页响应体
│   │       └── ResultCode.java           # 响应码枚举
│   ├── module/
│   │   ├── auth/
│   │   │   ├── controller/AuthController.java
│   │   │   ├── dto/LoginDTO.java
│   │   │   └── vo/LoginVO.java           # 含 token、用户信息、权限列表
│   │   ├── user/
│   │   │   ├── controller/UserController.java
│   │   │   ├── service/UserService.java + impl/
│   │   │   ├── mapper/UserMapper.java + UserMapper.xml
│   │   │   ├── entity/SysUser.java
│   │   │   ├── dto/UserSaveDTO.java / UserUpdateDTO.java / UserQueryDTO.java
│   │   │   └── vo/UserVO.java
│   │   ├── role/                         # 同上结构
│   │   ├── menu/                         # 同上结构
│   │   └── log/                          # 操作日志 + 登录日志
└── resources/
    ├── application.yml
    ├── application-dev.yml
    └── mapper/                           # XML 映射文件
```

---

## 五、核心后端实现规范

### 5.1 Sa-Token 集成

```java
// 实现 StpInterface，从数据库/缓存加载权限和角色
@Component
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 根据 userId 查询用户所有权限标识（sys:user:list 格式）
        // 优先从 Redis 缓存读取，缓存未命中则查库并写入缓存
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 根据 userId 查询用户所有角色 code
    }
}
```

**认证接口设计：**

```java
// POST /auth/login
// 1. 校验账号密码
// 2. 检查用户状态
// 3. StpUtil.login(userId) 登录
// 4. 缓存权限信息到 Redis
// 5. 记录登录日志
// 6. 返回 LoginVO（tokenInfo + userInfo + permissions + menus）

// POST /auth/logout
// StpUtil.logout() + 清除 Redis 权限缓存

// GET /auth/info
// 返回当前登录用户信息、权限列表、菜单树（用于前端动态路由）
```

**Sa-Token 配置：**

```yaml
sa-token:
  token-name: Authorization
  timeout: 86400          # Token 有效期（秒）：1天
  active-timeout: 1800    # 动态续签：30分钟无操作自动续签
  is-concurrent: false    # 禁止同账号多端同时登录
  is-share: false
  token-style: uuid
  is-log: false
```

### 5.2 MyBatis Plus 配置

```java
@Configuration
public class MybatisPlusConfig {

    // 分页插件
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    // 雪花ID 配置（workerId + datacenterId 从配置读取）
    @Bean
    public IdentifierGenerator identifierGenerator() {
        return new DefaultIdentifierGenerator(new WorkerIdUtil().getWorkerId());
    }
}

// 自动填充
@Component
public class MetaObjectHandlerImpl implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "createBy", () -> StpUtil.getLoginIdAsLong(), Long.class);
        // updateTime / updateBy 同理
    }
}
```

### 5.3 统一响应体

```java
@Data
public class R<T> {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public static <T> R<T> ok(T data) { ... }
    public static <T> R<T> ok() { ... }
    public static <T> R<T> fail(String message) { ... }
    public static <T> R<T> fail(ResultCode resultCode) { ... }
}
```

### 5.4 操作日志 AOP

```java
@Log(module = "用户管理", action = "新增用户")
@SaCheckPermission("sys:user:add")
@PostMapping
public R<Void> save(@Valid @RequestBody UserSaveDTO dto) { ... }
```

**LogAspect 必须记录以下字段（异步写库，使用 `@Async`）：**

```
模块名（module）、操作名（action）
请求 URL、请求方式（GET/POST/...）
请求参数（JSON 序列化，敏感参数如 password 字段自动脱敏替换为 "***"）
响应结果（JSON，超过 2000 字符截断）
执行耗时（ms）
操作人 ID、操作人用户名（从 Sa-Token 会话获取）
客户端真实 IP（ip 字段）
IP 归属地（ip_location 字段）
操作状态（正常/异常）、异常信息
```

**IP 获取与解析工具类（IpUtil.java）：**

```java
// 1. 获取真实 IP（需处理代理场景，依次检查以下 Header）
//    X-Forwarded-For → X-Real-IP → Proxy-Client-IP → WL-Proxy-Client-IP → 直连 IP
//    注意：X-Forwarded-For 可能含多个 IP，取第一个非 unknown 的值

// 2. IP 归属地解析
//    使用 Hutool 的 cn.hutool.extra.ip 包（需引入 ip2region 依赖），
//    或直接使用 ip2region 的离线库（推荐，无需网络请求）：
//    implementation 'org.lionsoul:ip2region:2.7.0'
//    
//    解析结果格式："中国|广东省|深圳市"，去掉"0|"占位符
//    本地回环地址（127.0.0.1 / ::1）返回"本地访问"
//    内网 IP（10.x / 192.168.x / 172.16-31.x）返回"内网IP"

public class IpUtil {
    public static String getRealIp(HttpServletRequest request) { ... }
    public static String getIpLocation(String ip) { ... }  // 调用 ip2region 离线解析
}
```

> ⚠️ **ip2region 依赖需同步加入 build.gradle**：`implementation 'org.lionsoul:ip2region:2.7.0'`
> 离线数据库文件 `ip2region.xdb` 放在 `src/main/resources/ip2region/` 目录下。

**登录日志同样记录 IP 和归属地**，在 `AuthController.login()` 方法中调用 `IpUtil` 后写入 `sys_log_login` 表，无论登录成功还是失败都需记录。

### 5.5 菜单树递归构建

```java
// 查询所有菜单后在内存中递归构建树，避免多次 DB 查询
public List<MenuTreeVO> buildTree(List<SysMenu> menus) {
    Map<Long, MenuTreeVO> menuMap = menus.stream()
        .collect(Collectors.toMap(SysMenu::getId, this::toVO));
    List<MenuTreeVO> roots = new ArrayList<>();
    menuMap.values().forEach(menu -> {
        if (menu.getParentId() == 0L) {
            roots.add(menu);
        } else {
            menuMap.get(menu.getParentId()).getChildren().add(menu);
        }
    });
    return roots;
}
```

---

## 六、前端项目结构

```
frontend/
├── public/
├── src/
│   ├── api/                    # 接口请求层（按模块划分）
│   │   ├── auth.ts
│   │   ├── user.ts
│   │   ├── role.ts
│   │   ├── menu.ts
│   │   └── log.ts
│   ├── assets/                 # 静态资源
│   ├── components/             # 全局公共组件
│   │   ├── IconSelect/         # 图标选择器
│   │   ├── PageContainer/      # 页面容器（含面包屑）
│   │   └── SvgIcon/
│   ├── layout/                 # 布局组件
│   │   ├── index.vue           # 主布局（侧边栏 + Header + 内容区）
│   │   ├── components/
│   │   │   ├── Sidebar.vue     # 左侧菜单（支持折叠）
│   │   │   ├── Header.vue      # 顶栏（面包屑、用户信息、退出）
│   │   │   └── TagsView.vue    # 多页签（可选）
│   ├── router/
│   │   ├── index.ts            # 路由实例 + 路由守卫
│   │   ├── staticRoutes.ts     # 静态路由（登录页等）
│   │   └── dynamicRoutes.ts    # 动态路由生成工具函数
│   ├── stores/
│   │   ├── auth.ts             # 用户信息、Token、登录/登出
│   │   ├── menu.ts             # 菜单树、动态路由
│   │   └── app.ts              # 应用全局状态（侧边栏折叠等）
│   ├── types/
│   │   ├── api.d.ts            # 接口响应类型
│   │   ├── user.d.ts
│   │   ├── role.d.ts
│   │   └── menu.d.ts
│   ├── utils/
│   │   ├── request.ts          # Axios 封装（拦截器、错误处理）
│   │   ├── permission.ts       # 权限指令 v-permission
│   │   └── common.ts           # 通用工具函数
│   ├── views/
│   │   ├── login/
│   │   │   └── index.vue       # 登录页
│   │   ├── dashboard/
│   │   │   └── index.vue       # 首页仪表盘
│   │   ├── system/
│   │   │   ├── user/
│   │   │   │   └── index.vue   # 用户管理
│   │   │   ├── role/
│   │   │   │   └── index.vue   # 角色管理
│   │   │   └── menu/
│   │   │       └── index.vue   # 菜单管理
│   │   ├── log/
│   │   │   ├── operate/
│   │   │   │   └── index.vue   # 操作日志
│   │   │   └── login/
│   │   │       └── index.vue   # 登录日志
│   │   └── profile/
│   │       └── index.vue       # 个人中心（基本信息 + 修改密码 + 登录记录）
│   ├── App.vue
│   └── main.ts
├── .env.development
├── .env.production
├── vite.config.ts
├── tsconfig.json
└── package.json
```

---

## 七、前端核心实现规范

### 7.1 Axios 封装（utils/request.ts）

```typescript
// 需包含：
// 1. baseURL 从环境变量读取
// 2. 请求拦截器：自动附加 Authorization Token（Sa-Token 格式）
// 3. 响应拦截器：
//    - code === 200 直接返回 data
//    - code === 401 清除本地状态，跳转登录页
//    - code === 403 提示无权限
//    - 其他错误 message.error 提示
// 4. 请求/响应 TypeScript 泛型封装
//    export function request<T>(config: AxiosRequestConfig): Promise<T>
```

### 7.2 认证 Store（stores/auth.ts）

```typescript
export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const permissions = ref<string[]>([])
  const roles = ref<string[]>([])

  async function login(data: LoginParams) {
    const res = await authApi.login(data)
    token.value = res.tokenValue
    localStorage.setItem('token', res.tokenValue)
    await fetchUserInfo()
  }

  async function fetchUserInfo() {
    const res = await authApi.getInfo()
    userInfo.value = res.userInfo
    permissions.value = res.permissions
    roles.value = res.roles
    // 触发动态路由生成
    useMenuStore().generateRoutes(res.menus)
  }

  async function logout() {
    await authApi.logout()
    token.value = ''
    userInfo.value = null
    permissions.value = []
    localStorage.removeItem('token')
    router.push('/login')
  }

  // 权限判断方法
  function hasPermission(permission: string): boolean {
    return permissions.value.includes(permission)
  }

  return { token, userInfo, permissions, roles, login, logout, fetchUserInfo, hasPermission }
})
```

### 7.3 动态路由生成（router/index.ts）

```typescript
// 路由守卫逻辑：
// 1. 白名单路由（/login）直接放行
// 2. 未登录（无 Token）跳转 /login
// 3. 已登录访问 /login 跳转首页
// 4. 已登录但未加载用户信息 → 调用 fetchUserInfo() 触发动态路由注册
// 5. 已登录访问不存在路由 → 跳转 404

// 动态路由从后端 /auth/info 返回的 menus 数据生成：
// menu.component 字段值为字符串路径，前端通过 import.meta.glob 动态导入组件
const modules = import.meta.glob('../views/**/*.vue')

function generateRoutes(menus: MenuItem[]): RouteRecordRaw[] {
  return menus
    .filter(menu => menu.menuType !== 2) // 过滤按钮
    .map(menu => ({
      path: menu.path,
      name: menu.name,
      component: menu.component ? modules[`../views/${menu.component}.vue`] : Layout,
      meta: { title: menu.title, icon: menu.icon, permission: menu.permission },
      children: menu.children ? generateRoutes(menu.children) : []
    }))
}
```

### 7.4 权限指令（utils/permission.ts）

```typescript
// 全局自定义指令 v-permission
// 用法：<a-button v-permission="'sys:user:add'">新增</a-button>
// 无权限时自动移除 DOM 元素

export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const { value } = binding
    const authStore = useAuthStore()
    if (value && !authStore.hasPermission(value)) {
      el.parentNode?.removeChild(el)
    }
  }
}
```

### 7.5 各管理页面规范

每个管理页面（用户/角色/菜单）需包含：

```
1. 顶部搜索栏（a-form inline 布局，含搜索/重置按钮）
2. 操作按钮区（新增，带 v-permission 控制）
3. 数据表格（a-table，含分页、loading 状态、列操作按钮）
4. 新增/编辑 Modal（a-modal + a-form，含表单验证）
5. 删除确认（a-popconfirm）
6. 完整 TypeScript 类型标注
7. 使用 Composition API + <script setup> 语法
```

**用户管理页额外需要：**
- 分配角色 Modal（a-transfer 穿梭框或 a-checkbox-group）
- 重置密码功能

**角色管理页额外需要：**
- 分配权限 Modal（a-tree 树形复选框展示菜单权限）

**菜单管理页额外需要：**
- 树形表格展示（a-table rowKey + treeData）
- 图标选择器组件（展示 @ant-design/icons-vue 所有图标，支持搜索过滤）
- 菜单类型切换时动态显示不同表单字段（目录/菜单/按钮三种表单字段差异化）

**个人中心页（profile/index.vue）需要：**

```
Tab 切换三个面板：

1. 基本信息
   - 左侧：头像展示 + a-upload 上传（限制 JPG/PNG，2MB 以内，上传后预览）
   - 右侧：昵称、邮箱、手机号可编辑表单
   - 提交调用 PUT /profile/info

2. 修改密码
   - 旧密码输入框（password 类型）
   - 新密码输入框（含强度提示：至少8位、包含字母和数字）
   - 确认新密码（前端校验与新密码一致）
   - 提交调用 PUT /profile/password
   - 修改成功后自动登出，跳转登录页

3. 登录记录
   - 展示当前用户最近 20 条登录日志（只读）
   - 列：登录时间、IP地址、登录地点、浏览器、操作系统、登录结果（成功/失败标签）
   - 调用 GET /profile/login-logs
```

**对应后端接口（module/profile）：**

```java
GET    /profile/info           # 获取当前用户基本信息
PUT    /profile/info           # 修改基本信息（昵称/邮箱/手机号）
PUT    /profile/avatar         # 上传头像（multipart/form-data）
PUT    /profile/password       # 修改密码（需校验旧密码）
GET    /profile/login-logs     # 当前用户登录记录（分页，默认20条）
```

**Header 联动：** 右上角用户名/头像下拉菜单包含「个人中心」和「退出登录」两项，点击个人中心路由跳转 `/profile`。

---

## 八、配置文件

### application.yml（后端）

> **由 AI 自行生成**，需包含以下配置项（请根据 Spring Boot 4.x 最新规范自行编写完整 yml）：
>
> - 数据源（MySQL，含连接池 HikariCP 参数）
> - Redis 连接（含连接池配置）
> - MyBatis Plus（mapper 扫描路径、雪花ID、逻辑删除、下划线转驼峰、开发环境打印 SQL）
> - Sa-Token（token-name: Authorization、有效期 86400s、动态续签 1800s、禁止多端同登、uuid 风格）
> - 异步线程池（供 `@Async` 日志写库使用，核心线程数 2、最大 10、队列 100）
> - 日志级别（dev 环境开启 debug）
> - 多环境配置（`application-dev.yml` 存放本地配置，`application-prod.yml` 存放生产配置，敏感信息不提交）

### vite.config.ts（前端）

```typescript
export default defineConfig({
  plugins: [vue()],
  resolve: { alias: { '@': '/src' } },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, '')
      }
    }
  },
  css: {
    preprocessorOptions: {
      less: {
        modifyVars: { '@primary-color': '#1677ff' },
        javascriptEnabled: true
      }
    }
  }
})
```

### .env.development（前端）

```env
VITE_APP_TITLE=后台管理系统
VITE_APP_BASE_API=/api
```

---

## 九、开发顺序（推荐分轮对话）

| 轮次 | 内容 | 建议提示词补充 |
|------|------|--------------|
| 第 1 轮 | 数据库 SQL + 后端项目初始化 | "请生成完整建表SQL和 build.gradle，以及项目入口配置" |
| 第 2 轮 | 后端公共基础代码 | "请生成 R.java、BusinessException、GlobalExceptionHandler、MyBatisPlus配置" |
| 第 3 轮 | Sa-Token 认证模块 | "请实现 StpInterfaceImpl、AuthController、登录日志记录" |
| 第 4 轮 | 用户管理模块（后端） | "请实现 User 模块完整 CRUD，含分页查询、角色分配接口" |
| 第 5 轮 | 角色 + 菜单模块（后端） | "请实现 Role 和 Menu 模块，含权限分配和菜单树接口" |
| 第 6 轮 | 操作日志 AOP（后端） | "请实现 @Log 注解和 LogAspect 切面" |
| 第 7 轮 | 个人中心模块（后端） | "请实现 profile 模块：基本信息修改、头像上传、修改密码、登录记录查询" |
| 第 8 轮 | 前端工程初始化 | "请生成 package.json、vite.config.ts、tsconfig.json、main.ts、App.vue、全局类型声明" |
| 第 9 轮 | 前端 Axios + Store + Router | "请实现 request.ts（含拦截器）、auth store、menu store、app store、路由守卫和动态路由生成" |
| 第 10 轮 | 登录页 + Layout 布局 | "请实现登录页（含表单验证动效）和主布局（可折叠侧边栏+Header下拉菜单+面包屑）" |
| 第 11 轮 | 用户/角色/菜单管理页 | "请分别实现三个管理页面，含完整 CRUD、角色分配穿梭框、权限树、图标选择器" |
| 第 12 轮 | 日志查看页 + 个人中心页 | "请实现操作日志/登录日志查看页（含导出），以及个人中心三Tab页（基本信息/修改密码/登录记录）" |
| 第 13 轮 | 权限指令 + 收尾优化 | "请实现 v-permission 指令、404/403 页面，并全局检查所有 TODO 和类型错误" |

---

## 十、代码质量硬性要求

1. **零占位代码**：禁止出现 `// TODO`、`// implement me`、空方法体
2. **完整类型标注**：前端所有函数参数、返回值必须有 TypeScript 类型，禁用 `any`（特殊场景用 `unknown` 替代）
3. **统一错误处理**：后端用 `GlobalExceptionHandler`，前端用 Axios 响应拦截器，不允许在业务代码中 `catch` 后静默忽略
4. **逻辑删除**：所有查询自动过滤 `deleted = 1`（MyBatis Plus 自动处理）
5. **权限粒度**：每个接口都要加 `@SaCheckPermission`，前端按钮都要加 `v-permission`
6. **敏感字段**：`SysUser.password` 在 VO/响应中必须脱敏（`@JsonIgnore` 或 MapStruct 映射时排除）
7. **中文注释**：所有 Entity 字段、接口方法、关键逻辑必须有中文注释
8. **Sa-Token 与 Spring Boot 4.x 兼容性**：如存在 API 变更，请主动说明并给出正确用法
9. **前端 loading 状态**：所有异步请求期间需展示 loading（表格用 a-table 的 loading 属性，按钮用 :loading）
10. **表单重置**：Modal 关闭时必须重置表单状态，防止下次打开时残留旧数据
11. **404 / 403 页面**：路由未匹配时展示 404 页，无权限时展示 403 页，均含返回首页按钮

---