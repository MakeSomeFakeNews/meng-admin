package me.dqq.admin.module.menu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dqq.admin.common.annotation.Log;
import me.dqq.admin.common.result.R;
import me.dqq.admin.module.menu.dto.MenuSaveDTO;
import me.dqq.admin.module.menu.service.MenuService;
import me.dqq.admin.module.menu.vo.MenuTreeVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/tree")
    public R<List<MenuTreeVO>> tree() {
        return R.ok(menuService.getAllMenuTree());
    }

    @GetMapping("/list")
    @SaCheckPermission("sys:menu:list")
    public R<List<MenuTreeVO>> list() {
        return R.ok(menuService.getAllMenuTree());
    }

    @PostMapping
    @SaCheckPermission("sys:menu:add")
    @Log(module = "菜单管理", action = "新增菜单")
    public R<Void> save(@Valid @RequestBody MenuSaveDTO dto) {
        menuService.saveMenu(dto);
        return R.ok();
    }

    @PutMapping
    @SaCheckPermission("sys:menu:update")
    @Log(module = "菜单管理", action = "编辑菜单")
    public R<Void> update(@Valid @RequestBody MenuSaveDTO dto) {
        menuService.updateMenu(dto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:menu:delete")
    @Log(module = "菜单管理", action = "删除菜单")
    public R<Void> delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return R.ok();
    }
}
