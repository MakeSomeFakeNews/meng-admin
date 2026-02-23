package me.dqq.admin.common.config;

import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dqq.admin.common.constant.RedisKeyConst;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Sa-Token 权限接口实现（从缓存/数据库加载权限和角色）
 * 注意：这里实现了 StpInterface，并以 @Component 注册
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaTokenConfig implements StpInterface {

    private final RedisTemplate<String, Object> redisTemplate;

    // 注意：这里使用 ApplicationContext 延迟加载 UserMapper/RoleMapper 避免循环依赖
    // 实际权限数据从 Redis 缓存读取，缓存由 login 时写入

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getPermissionList(Object loginId, String loginType) {
        String key = RedisKeyConst.USER_PERMISSIONS + loginId;
        List<String> permissions = (List<String>) redisTemplate.opsForValue().get(key);
        if (permissions == null) {
            // 缓存未命中时返回空列表（权限应在登录时写入缓存）
            log.warn("用户权限缓存未命中，userId={}", loginId);
            return List.of();
        }
        return permissions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getRoleList(Object loginId, String loginType) {
        String key = RedisKeyConst.USER_ROLES + loginId;
        List<String> roles = (List<String>) redisTemplate.opsForValue().get(key);
        if (roles == null) {
            log.warn("用户角色缓存未命中，userId={}", loginId);
            return List.of();
        }
        return roles;
    }

    /**
     * 缓存用户权限信息到 Redis
     */
    public void cachePermissions(Long userId, List<String> permissions, List<String> roles) {
        String permKey = RedisKeyConst.USER_PERMISSIONS + userId;
        String roleKey = RedisKeyConst.USER_ROLES + userId;
        redisTemplate.opsForValue().set(permKey, permissions, RedisKeyConst.PERMISSION_EXPIRE, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(roleKey, roles, RedisKeyConst.PERMISSION_EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 清除用户权限缓存
     */
    public void clearPermissions(Long userId) {
        redisTemplate.delete(RedisKeyConst.USER_PERMISSIONS + userId);
        redisTemplate.delete(RedisKeyConst.USER_ROLES + userId);
    }
}
