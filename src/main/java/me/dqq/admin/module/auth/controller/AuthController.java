package me.dqq.admin.module.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dqq.admin.common.result.R;
import me.dqq.admin.module.auth.dto.LoginDTO;
import me.dqq.admin.module.auth.vo.LoginVO;
import me.dqq.admin.module.auth.service.AuthService;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        LoginVO loginVO = authService.login(dto, request);
        return R.ok(loginVO);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        authService.logout();
        return R.ok();
    }

    /**
     * 获取当前登录用户信息（权限、菜单）
     */
    @GetMapping("/info")
    public R<LoginVO> getInfo() {
        LoginVO loginVO = authService.getCurrentUserInfo();
        return R.ok(loginVO);
    }
}
