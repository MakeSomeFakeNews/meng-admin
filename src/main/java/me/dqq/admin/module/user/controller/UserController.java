package me.dqq.admin.module.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dqq.admin.common.annotation.Log;
import me.dqq.admin.common.result.PageResult;
import me.dqq.admin.common.result.R;
import me.dqq.admin.module.user.dto.UserQueryDTO;
import me.dqq.admin.module.user.dto.UserSaveDTO;
import me.dqq.admin.module.user.dto.UserUpdateDTO;
import me.dqq.admin.module.user.service.UserService;
import me.dqq.admin.module.user.vo.UserVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理接口
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 分页查询用户列表 */
    @GetMapping("/page")
    @SaCheckPermission("sys:user:list")
    public R<PageResult<UserVO>> page(UserQueryDTO query) {
        return R.ok(userService.pageList(query));
    }

    /** 获取用户详情 */
    @GetMapping("/{id}")
    @SaCheckPermission("sys:user:list")
    public R<UserVO> getById(@PathVariable Long id) {
        var user = userService.getById(id);
        if (user == null) return R.fail("用户不存在");
        var vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setStatus(user.getStatus());
        return R.ok(vo);
    }

    /** 新增用户 */
    @PostMapping
    @SaCheckPermission("sys:user:add")
    @Log(module = "用户管理", action = "新增用户")
    public R<Void> save(@Valid @RequestBody UserSaveDTO dto) {
        userService.saveUser(dto);
        return R.ok();
    }

    /** 编辑用户 */
    @PutMapping
    @SaCheckPermission("sys:user:update")
    @Log(module = "用户管理", action = "编辑用户")
    public R<Void> update(@Valid @RequestBody UserUpdateDTO dto) {
        userService.updateUser(dto);
        return R.ok();
    }

    /** 删除用户 */
    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:user:delete")
    @Log(module = "用户管理", action = "删除用户")
    public R<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return R.ok();
    }

    /** 重置密码 */
    @PutMapping("/reset-password")
    @SaCheckPermission("sys:user:reset-pwd")
    @Log(module = "用户管理", action = "重置密码")
    public R<Void> resetPassword(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String newPassword = body.get("newPassword").toString();
        userService.resetPassword(userId, newPassword);
        return R.ok();
    }

    /** 修改状态 */
    @PutMapping("/{id}/status")
    @SaCheckPermission("sys:user:update")
    @Log(module = "用户管理", action = "修改用户状态")
    public R<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.updateStatus(id, body.get("status"));
        return R.ok();
    }

    /** 获取用户已分配的角色ID */
    @GetMapping("/{id}/roles")
    @SaCheckPermission("sys:user:list")
    public R<List<Long>> getUserRoles(@PathVariable Long id) {
        return R.ok(userService.getUserRoleIds(id));
    }

    /** 分配用户角色 */
    @PutMapping("/{id}/roles")
    @SaCheckPermission("sys:user:assign-role")
    @Log(module = "用户管理", action = "分配角色")
    public R<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return R.ok();
    }
}
