package me.dqq.admin.module.role.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dqq.admin.common.annotation.Log;
import me.dqq.admin.common.result.PageResult;
import me.dqq.admin.common.result.R;
import me.dqq.admin.module.role.dto.RoleQueryDTO;
import me.dqq.admin.module.role.dto.RoleSaveDTO;
import me.dqq.admin.module.role.service.RoleService;
import me.dqq.admin.module.role.vo.RoleVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/page")
    @SaCheckPermission("sys:role:list")
    public R<PageResult<RoleVO>> page(RoleQueryDTO query) {
        return R.ok(roleService.pageList(query));
    }

    @GetMapping("/list")
    public R<List<RoleVO>> list() {
        return R.ok(roleService.listAll());
    }

    @PostMapping
    @SaCheckPermission("sys:role:add")
    @Log(module = "角色管理", action = "新增角色")
    public R<Void> save(@Valid @RequestBody RoleSaveDTO dto) {
        roleService.saveRole(dto);
        return R.ok();
    }

    @PutMapping
    @SaCheckPermission("sys:role:update")
    @Log(module = "角色管理", action = "编辑角色")
    public R<Void> update(@Valid @RequestBody RoleSaveDTO dto) {
        roleService.updateRole(dto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:role:delete")
    @Log(module = "角色管理", action = "删除角色")
    public R<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return R.ok();
    }

    @GetMapping("/{id}/menus")
    @SaCheckPermission("sys:role:list")
    public R<List<Long>> getRoleMenus(@PathVariable Long id) {
        return R.ok(roleService.getRoleMenuIds(id));
    }

    @PutMapping("/assign-menus")
    @SaCheckPermission("sys:role:assign-menu")
    @Log(module = "角色管理", action = "分配权限")
    public R<Void> assignMenus(@RequestBody Map<String, Object> body) {
        Long roleId = Long.valueOf(body.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Integer> menuIdInts = (List<Integer>) body.get("menuIds");
        List<Long> menuIds = menuIdInts.stream().map(Long::valueOf).collect(Collectors.toList());
        roleService.assignMenus(roleId, menuIds);
        return R.ok();
    }
}
