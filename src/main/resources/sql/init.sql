-- 创建数据库
CREATE DATABASE IF NOT EXISTS meng_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE meng_admin;

-- sys_user 用户表
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法）',
  `username` VARCHAR(64) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1正常/0禁用）',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除（0正常/1已删除）',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- sys_role 角色表
CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL COMMENT '主键ID',
  `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(64) NOT NULL COMMENT '角色编码（唯一）',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1正常/0禁用）',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_by` BIGINT DEFAULT NULL,
  `update_by` BIGINT DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- sys_menu 菜单/权限表
CREATE TABLE `sys_menu` (
  `id` BIGINT NOT NULL COMMENT '主键ID',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID（0为顶级）',
  `title` VARCHAR(64) NOT NULL COMMENT '菜单标题',
  `name` VARCHAR(64) DEFAULT NULL COMMENT '路由name',
  `path` VARCHAR(255) DEFAULT NULL COMMENT '路由路径',
  `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
  `icon` VARCHAR(64) DEFAULT NULL COMMENT '图标',
  `menu_type` TINYINT NOT NULL DEFAULT 0 COMMENT '菜单类型（0目录/1菜单/2按钮）',
  `permission` VARCHAR(128) DEFAULT NULL COMMENT '权限标识（如sys:user:list）',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `visible` TINYINT NOT NULL DEFAULT 1 COMMENT '是否可见（1是/0否）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1正常/0禁用）',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_by` BIGINT DEFAULT NULL,
  `update_by` BIGINT DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单/权限表';

-- sys_user_role 用户角色关联表
CREATE TABLE `sys_user_role` (
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- sys_role_menu 角色菜单关联表
CREATE TABLE `sys_role_menu` (
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- sys_log_operate 操作日志表
CREATE TABLE `sys_log_operate` (
  `id` BIGINT NOT NULL COMMENT '主键ID',
  `module` VARCHAR(64) DEFAULT NULL COMMENT '模块名',
  `action` VARCHAR(64) DEFAULT NULL COMMENT '操作名',
  `method` VARCHAR(255) DEFAULT NULL COMMENT '方法名',
  `request_url` VARCHAR(255) DEFAULT NULL COMMENT '请求URL',
  `request_method` VARCHAR(16) DEFAULT NULL COMMENT '请求方式',
  `request_params` TEXT DEFAULT NULL COMMENT '请求参数（JSON）',
  `response_result` TEXT DEFAULT NULL COMMENT '响应结果（JSON，最多2000字符）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1正常/0异常）',
  `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(64) DEFAULT NULL COMMENT '操作人用户名',
  `ip` VARCHAR(64) DEFAULT NULL COMMENT '客户端IP',
  `ip_location` VARCHAR(128) DEFAULT NULL COMMENT 'IP归属地',
  `duration` BIGINT DEFAULT NULL COMMENT '执行耗时（ms）',
  `create_time` DATETIME DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- sys_log_login 登录日志表
CREATE TABLE `sys_log_login` (
  `id` BIGINT NOT NULL COMMENT '主键ID',
  `username` VARCHAR(64) DEFAULT NULL COMMENT '用户名',
  `ip` VARCHAR(64) DEFAULT NULL COMMENT '登录IP',
  `ip_location` VARCHAR(128) DEFAULT NULL COMMENT 'IP归属地',
  `browser` VARCHAR(128) DEFAULT NULL COMMENT '浏览器',
  `os` VARCHAR(128) DEFAULT NULL COMMENT '操作系统',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '登录状态（1成功/0失败）',
  `message` VARCHAR(255) DEFAULT NULL COMMENT '提示信息',
  `login_time` DATETIME DEFAULT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- 初始化超级管理员（密码: Admin@123456，BCrypt加密）
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `status`, `deleted`, `create_time`, `update_time`)
VALUES (1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 'admin@example.com', 1, 0, NOW(), NOW());

-- 初始化超级管理员角色
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `sort`, `status`, `remark`, `deleted`, `create_time`, `update_time`)
VALUES (1, '超级管理员', 'SUPER_ADMIN', 0, 1, '超级管理员拥有所有权限', 0, NOW(), NOW());

-- 绑定用户角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 初始化菜单数据
INSERT INTO `sys_menu` (`id`, `parent_id`, `title`, `name`, `path`, `component`, `icon`, `menu_type`, `permission`, `sort`, `visible`, `status`, `deleted`, `create_time`, `update_time`) VALUES
-- 顶级目录
(1, 0, '系统管理', 'System', '/system', NULL, 'SettingOutlined', 0, NULL, 1, 1, 1, 0, NOW(), NOW()),
(2, 0, '日志管理', 'Log', '/log', NULL, 'FileTextOutlined', 0, NULL, 2, 1, 1, 0, NOW(), NOW()),
-- 系统管理子菜单
(10, 1, '用户管理', 'SystemUser', '/system/user', 'system/user/index', 'UserOutlined', 1, 'sys:user:list', 1, 1, 1, 0, NOW(), NOW()),
(11, 1, '角色管理', 'SystemRole', '/system/role', 'system/role/index', 'TeamOutlined', 1, 'sys:role:list', 2, 1, 1, 0, NOW(), NOW()),
(12, 1, '菜单管理', 'SystemMenu', '/system/menu', 'system/menu/index', 'MenuOutlined', 1, 'sys:menu:list', 3, 1, 1, 0, NOW(), NOW()),
-- 日志管理子菜单
(20, 2, '操作日志', 'LogOperate', '/log/operate', 'log/operate/index', 'AuditOutlined', 1, 'sys:log:operate:list', 1, 1, 1, 0, NOW(), NOW()),
(21, 2, '登录日志', 'LogLogin', '/log/login', 'log/login/index', 'LoginOutlined', 1, 'sys:log:login:list', 2, 1, 1, 0, NOW(), NOW()),
-- 用户管理按钮权限
(100, 10, '新增用户', NULL, NULL, NULL, NULL, 2, 'sys:user:add', 1, 1, 1, 0, NOW(), NOW()),
(101, 10, '编辑用户', NULL, NULL, NULL, NULL, 2, 'sys:user:update', 2, 1, 1, 0, NOW(), NOW()),
(102, 10, '删除用户', NULL, NULL, NULL, NULL, 2, 'sys:user:delete', 3, 1, 1, 0, NOW(), NOW()),
(103, 10, '重置密码', NULL, NULL, NULL, NULL, 2, 'sys:user:reset-pwd', 4, 1, 1, 0, NOW(), NOW()),
(104, 10, '分配角色', NULL, NULL, NULL, NULL, 2, 'sys:user:assign-role', 5, 1, 1, 0, NOW(), NOW()),
-- 角色管理按钮权限
(110, 11, '新增角色', NULL, NULL, NULL, NULL, 2, 'sys:role:add', 1, 1, 1, 0, NOW(), NOW()),
(111, 11, '编辑角色', NULL, NULL, NULL, NULL, 2, 'sys:role:update', 2, 1, 1, 0, NOW(), NOW()),
(112, 11, '删除角色', NULL, NULL, NULL, NULL, 2, 'sys:role:delete', 3, 1, 1, 0, NOW(), NOW()),
(113, 11, '分配权限', NULL, NULL, NULL, NULL, 2, 'sys:role:assign-menu', 4, 1, 1, 0, NOW(), NOW()),
-- 菜单管理按钮权限
(120, 12, '新增菜单', NULL, NULL, NULL, NULL, 2, 'sys:menu:add', 1, 1, 1, 0, NOW(), NOW()),
(121, 12, '编辑菜单', NULL, NULL, NULL, NULL, 2, 'sys:menu:update', 2, 1, 1, 0, NOW(), NOW()),
(122, 12, '删除菜单', NULL, NULL, NULL, NULL, 2, 'sys:menu:delete', 3, 1, 1, 0, NOW(), NOW());

-- 超级管理员拥有所有菜单权限（关联所有菜单）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `sys_menu` WHERE `deleted` = 0;
