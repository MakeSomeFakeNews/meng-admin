package me.dqq.admin.module.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.dqq.admin.module.log.entity.SysLogLogin;
import me.dqq.admin.module.log.mapper.LogLoginMapper;
import me.dqq.admin.module.log.service.LogLoginService;
import org.springframework.stereotype.Service;

/**
 * 登录日志 Service 实现
 */
@Service
public class LogLoginServiceImpl extends ServiceImpl<LogLoginMapper, SysLogLogin>
        implements LogLoginService {
}
