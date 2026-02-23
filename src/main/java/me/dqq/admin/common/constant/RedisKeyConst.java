package me.dqq.admin.common.constant;

/**
 * Redis Key 常量
 */
public interface RedisKeyConst {

    /** 用户权限缓存 Key 前缀，格式：user:permissions:{userId} */
    String USER_PERMISSIONS = "user:permissions:";

    /** 用户角色缓存 Key 前缀，格式：user:roles:{userId} */
    String USER_ROLES = "user:roles:";

    /** 用户信息缓存 Key 前缀，格式：user:info:{userId} */
    String USER_INFO = "user:info:";

    /** 权限缓存过期时间（秒）：1小时 */
    long PERMISSION_EXPIRE = 3600L;
}
