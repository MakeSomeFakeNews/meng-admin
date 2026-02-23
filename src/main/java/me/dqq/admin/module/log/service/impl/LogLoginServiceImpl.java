package me.dqq.admin.module.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import me.dqq.admin.common.result.PageResult;
import me.dqq.admin.module.log.dto.LogLoginQueryDTO;
import me.dqq.admin.module.log.entity.SysLogLogin;
import me.dqq.admin.module.log.mapper.LogLoginMapper;
import me.dqq.admin.module.log.service.LogLoginService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 登录日志 Service 实现
 */
@Service
@RequiredArgsConstructor
public class LogLoginServiceImpl extends ServiceImpl<LogLoginMapper, SysLogLogin>
        implements LogLoginService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResult<SysLogLogin> pageList(LogLoginQueryDTO query) {
        Page<SysLogLogin> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<SysLogLogin> wrapper = new LambdaQueryWrapper<SysLogLogin>()
                .like(StringUtils.hasText(query.getUsername()), SysLogLogin::getUsername, query.getUsername())
                .eq(query.getStatus() != null, SysLogLogin::getStatus, query.getStatus())
                .ge(StringUtils.hasText(query.getStartTime()), SysLogLogin::getLoginTime,
                        StringUtils.hasText(query.getStartTime()) ? LocalDateTime.parse(query.getStartTime(), FORMATTER) : null)
                .le(StringUtils.hasText(query.getEndTime()), SysLogLogin::getLoginTime,
                        StringUtils.hasText(query.getEndTime()) ? LocalDateTime.parse(query.getEndTime(), FORMATTER) : null)
                .orderByDesc(SysLogLogin::getLoginTime);
        page(page, wrapper);
        return PageResult.of(page);
    }

    @Override
    public PageResult<SysLogLogin> pageListByUsername(String username, int current, int size) {
        Page<SysLogLogin> page = new Page<>(current, size);
        LambdaQueryWrapper<SysLogLogin> wrapper = new LambdaQueryWrapper<SysLogLogin>()
                .eq(SysLogLogin::getUsername, username)
                .orderByDesc(SysLogLogin::getLoginTime);
        page(page, wrapper);
        return PageResult.of(page);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        removeByIds(ids);
    }

    @Override
    public void clear() {
        remove(new LambdaQueryWrapper<>());
    }
}
