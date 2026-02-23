package me.dqq.admin.module.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import me.dqq.admin.common.result.PageResult;
import me.dqq.admin.module.log.dto.LogOperateQueryDTO;
import me.dqq.admin.module.log.entity.SysLogOperate;
import me.dqq.admin.module.log.mapper.LogOperateMapper;
import me.dqq.admin.module.log.service.LogOperateService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 操作日志 Service 实现
 */
@Service
@RequiredArgsConstructor
public class LogOperateServiceImpl extends ServiceImpl<LogOperateMapper, SysLogOperate>
        implements LogOperateService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResult<SysLogOperate> pageList(LogOperateQueryDTO query) {
        Page<SysLogOperate> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<SysLogOperate> wrapper = new LambdaQueryWrapper<SysLogOperate>()
                .like(StringUtils.hasText(query.getModule()), SysLogOperate::getModule, query.getModule())
                .like(StringUtils.hasText(query.getOperatorName()), SysLogOperate::getOperatorName, query.getOperatorName())
                .eq(query.getStatus() != null, SysLogOperate::getStatus, query.getStatus())
                .ge(StringUtils.hasText(query.getStartTime()), SysLogOperate::getCreateTime,
                        StringUtils.hasText(query.getStartTime()) ? LocalDateTime.parse(query.getStartTime(), FORMATTER) : null)
                .le(StringUtils.hasText(query.getEndTime()), SysLogOperate::getCreateTime,
                        StringUtils.hasText(query.getEndTime()) ? LocalDateTime.parse(query.getEndTime(), FORMATTER) : null)
                .orderByDesc(SysLogOperate::getCreateTime);
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
