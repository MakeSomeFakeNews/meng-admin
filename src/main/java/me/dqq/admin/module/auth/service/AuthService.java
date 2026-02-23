package me.dqq.admin.module.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import me.dqq.admin.module.auth.dto.LoginDTO;
import me.dqq.admin.module.auth.vo.LoginVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto, HttpServletRequest request);

    /**
     * 退出登录
     */
    void logout();

    /**
     * 获取当前登录用户信息
     */
    LoginVO getCurrentUserInfo();
}
