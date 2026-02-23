package me.dqq.admin.module.log.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import me.dqq.admin.common.result.PageResult;
import me.dqq.admin.common.result.R;
import me.dqq.admin.module.log.dto.LogLoginQueryDTO;
import me.dqq.admin.module.log.dto.LogOperateQueryDTO;
import me.dqq.admin.module.log.entity.SysLogLogin;
import me.dqq.admin.module.log.entity.SysLogOperate;
import me.dqq.admin.module.log.service.LogLoginService;
import me.dqq.admin.module.log.service.LogOperateService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class LogController {

    private final LogOperateService logOperateService;
    private final LogLoginService logLoginService;

    /** 分页查询操作日志 */
    @GetMapping("/operate/page")
    @SaCheckPermission("sys:log:operate:list")
    public R<PageResult<SysLogOperate>> operatePage(LogOperateQueryDTO query) {
        return R.ok(logOperateService.pageList(query));
    }

    /** 删除操作日志 */
    @DeleteMapping("/operate")
    @SaCheckPermission("sys:log:operate:list")
    public R<Void> deleteOperateLog(@RequestParam String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        logOperateService.deleteByIds(idList);
        return R.ok();
    }

    /** 清空操作日志 */
    @DeleteMapping("/operate/clear")
    @SaCheckPermission("sys:log:operate:list")
    public R<Void> clearOperateLog() {
        logOperateService.clear();
        return R.ok();
    }

    /** 分页查询登录日志 */
    @GetMapping("/login/page")
    @SaCheckPermission("sys:log:login:list")
    public R<PageResult<SysLogLogin>> loginPage(LogLoginQueryDTO query) {
        return R.ok(logLoginService.pageList(query));
    }

    /** 删除登录日志 */
    @DeleteMapping("/login")
    @SaCheckPermission("sys:log:login:list")
    public R<Void> deleteLoginLog(@RequestParam String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        logLoginService.deleteByIds(idList);
        return R.ok();
    }

    /** 清空登录日志 */
    @DeleteMapping("/login/clear")
    @SaCheckPermission("sys:log:login:list")
    public R<Void> clearLoginLog() {
        logLoginService.clear();
        return R.ok();
    }
}
