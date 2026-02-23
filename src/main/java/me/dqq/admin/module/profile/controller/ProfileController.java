package me.dqq.admin.module.profile.controller;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dqq.admin.common.result.PageResult;
import me.dqq.admin.common.result.R;
import me.dqq.admin.module.log.entity.SysLogLogin;
import me.dqq.admin.module.profile.dto.UpdateInfoDTO;
import me.dqq.admin.module.profile.dto.UpdatePasswordDTO;
import me.dqq.admin.module.profile.service.ProfileService;
import me.dqq.admin.module.user.vo.UserVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /** 获取当前用户基本信息 */
    @GetMapping("/info")
    public R<UserVO> getInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(profileService.getInfo(userId));
    }

    /** 修改基本信息 */
    @PutMapping("/info")
    public R<Void> updateInfo(@Valid @RequestBody UpdateInfoDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        profileService.updateInfo(userId, dto);
        return R.ok();
    }

    /** 上传头像 */
    @PutMapping("/avatar")
    public R<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = StpUtil.getLoginIdAsLong();
        String avatarUrl = profileService.uploadAvatar(userId, file);
        return R.ok(avatarUrl);
    }

    /** 修改密码 */
    @PutMapping("/password")
    public R<Void> updatePassword(@Valid @RequestBody UpdatePasswordDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        profileService.updatePassword(userId, dto);
        return R.ok();
    }

    /** 获取当前用户登录记录 */
    @GetMapping("/login-logs")
    public R<PageResult<SysLogLogin>> getLoginLogs(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(profileService.getLoginLogs(userId, current, size));
    }
}
