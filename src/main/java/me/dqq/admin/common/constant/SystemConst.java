package me.dqq.admin.common.constant;

/**
 * 系统常量
 */
public interface SystemConst {

    /** 超级管理员角色编码 */
    String SUPER_ADMIN_ROLE = "SUPER_ADMIN";

    /** 默认密码 */
    String DEFAULT_PASSWORD = "Admin@123456";

    /** 头像文件前缀 */
    String AVATAR_PATH_PREFIX = "/avatar/";

    /** 操作日志最大响应体长度 */
    int MAX_RESPONSE_LENGTH = 2000;

    /** 个人登录记录默认查询条数 */
    int LOGIN_LOG_DEFAULT_SIZE = 20;
}
