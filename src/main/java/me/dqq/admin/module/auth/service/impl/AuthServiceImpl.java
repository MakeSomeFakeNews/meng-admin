package me.dqq.admin.module.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dqq.admin.common.config.SaTokenConfig;
import me.dqq.admin.common.constant.SystemConst;
import me.dqq.admin.common.exception.BusinessException;
import me.dqq.admin.common.result.ResultCode;
import me.dqq.admin.common.utils.IpUtil;
import me.dqq.admin.common.utils.PasswordUtil;
import me.dqq.admin.module.auth.dto.LoginDTO;
import me.dqq.admin.module.auth.service.AuthService;
import me.dqq.admin.module.auth.vo.LoginVO;
import me.dqq.admin.module.log.entity.SysLogLogin;
import me.dqq.admin.module.log.service.LogLoginService;
import me.dqq.admin.module.menu.service.MenuService;
import me.dqq.admin.module.menu.vo.MenuTreeVO;
import me.dqq.admin.module.role.mapper.RoleMapper;
import me.dqq.admin.module.user.entity.SysUser;
import me.dqq.admin.module.user.service.UserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final MenuService menuService;
    private final LogLoginService logLoginService;
    private final SaTokenConfig saTokenConfig;
    private final RoleMapper roleMapper;

    @Override
    public LoginVO login(LoginDTO dto, HttpServletRequest request) {
        String ip = IpUtil.getRealIp(request);
        String ipLocation = IpUtil.getIpLocation(ip);
        // 解析 UA
        String userAgentStr = request.getHeader("User-Agent");
        UserAgent ua = UserAgentUtil.parse(userAgentStr);
        String browser = ua != null ? ua.getBrowser().getName() : "未知";
        String os = ua != null ? ua.getOs().getName() : "未知";

        try {
            // 1. 查询用户
            SysUser user = userService.getByUsername(dto.getUsername());
            if (user == null) {
                saveLoginLog(dto.getUsername(), ip, ipLocation, browser, os, 0, "用户不存在");
                throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
            }

            // 2. 校验密码
            if (!PasswordUtil.verify(dto.getPassword(), user.getPassword())) {
                saveLoginLog(dto.getUsername(), ip, ipLocation, browser, os, 0, "密码错误");
                throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
            }

            // 3. 检查状态
            if (user.getStatus() == null || user.getStatus() == 0) {
                saveLoginLog(dto.getUsername(), ip, ipLocation, browser, os, 0, "用户已禁用");
                throw new BusinessException(ResultCode.USER_DISABLED);
            }

            // 4. Sa-Token 登录
            StpUtil.login(user.getId());
            // 将用户名存入 Session，供日志AOP读取
            StpUtil.getSession().set("username", user.getUsername());

            // 5. 加载权限和角色
            List<String> roles = loadUserRoles(user.getId());
            List<String> permissions = loadUserPermissions(user.getId(), roles);

            // 6. 写入 Redis 缓存
            saTokenConfig.cachePermissions(user.getId(), permissions, roles);

            // 7. 记录登录日志（成功）
            saveLoginLog(dto.getUsername(), ip, ipLocation, browser, os, 1, "登录成功");

            // 8. 构建返回值
            return buildLoginVO(user, permissions, roles);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("登录异常", e);
            throw new BusinessException("登录失败，请稍后重试");
        }
    }

    @Override
    public void logout() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            StpUtil.logout();
            // 清除 Redis 权限缓存
            saTokenConfig.clearPermissions(userId);
        } catch (Exception e) {
            log.warn("退出登录异常: {}", e.getMessage());
        }
    }

    @Override
    public LoginVO getCurrentUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userService.getById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        List<String> roles = loadUserRoles(userId);
        List<String> permissions = loadUserPermissions(userId, roles);
        return buildLoginVO(user, permissions, roles);
    }

    /**
     * 加载用户权限标识列表
     */
    private List<String> loadUserPermissions(Long userId, List<String> roles) {
        // 超级管理员拥有所有权限
        if (roles.contains(SystemConst.SUPER_ADMIN_ROLE)) {
            return menuService.getAllPermissions();
        }
        return menuService.getPermissionsByUserId(userId);
    }

    /**
     * 加载用户角色编码列表
     */
    private List<String> loadUserRoles(Long userId) {
        return roleMapper.selectRoleCodesByUserId(userId);
    }

    /**
     * 构建登录响应 VO
     */
    private LoginVO buildLoginVO(SysUser user, List<String> permissions, List<String> roles) {
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        LoginVO vo = new LoginVO();
        vo.setTokenName(tokenInfo.getTokenName());
        vo.setTokenValue(tokenInfo.getTokenValue());

        LoginVO.UserInfoVO userInfoVO = new LoginVO.UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setNickname(user.getNickname());
        userInfoVO.setAvatar(user.getAvatar());
        vo.setUserInfo(userInfoVO);

        vo.setPermissions(permissions);
        vo.setRoles(roles);

        // 菜单树（只返回目录和菜单，过滤按钮）
        boolean isSuperAdmin = roles.contains(SystemConst.SUPER_ADMIN_ROLE);
        List<MenuTreeVO> menus = isSuperAdmin
                ? menuService.getAllMenuTree()
                : menuService.getMenuTreeByUserId(user.getId());
        vo.setMenus(menus);

        return vo;
    }

    /**
     * 异步保存登录日志
     */
    @Async
    public void saveLoginLog(String username, String ip, String ipLocation,
                             String browser, String os, int status, String message) {
        try {
            SysLogLogin loginLog = new SysLogLogin();
            loginLog.setUsername(username);
            loginLog.setIp(ip);
            loginLog.setIpLocation(ipLocation);
            loginLog.setBrowser(browser);
            loginLog.setOs(os);
            loginLog.setStatus(status);
            loginLog.setMessage(message);
            loginLog.setLoginTime(LocalDateTime.now());
            logLoginService.save(loginLog);
        } catch (Exception e) {
            log.warn("保存登录日志失败: {}", e.getMessage());
        }
    }
}
