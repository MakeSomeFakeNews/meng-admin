package me.dqq.admin.module.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.dqq.admin.module.log.entity.SysLogOperate;
import me.dqq.admin.module.log.mapper.LogOperateMapper;
import me.dqq.admin.module.log.service.LogOperateService;
import org.springframework.stereotype.Service;

/**
 * 操作日志 Service 实现
 */
@Service
public class LogOperateServiceImpl extends ServiceImpl<LogOperateMapper, SysLogOperate>
        implements LogOperateService {
}
