package me.dqq.admin.module.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.dqq.admin.common.result.PageResult;
import me.dqq.admin.module.log.dto.LogLoginQueryDTO;
import me.dqq.admin.module.log.entity.SysLogLogin;

import java.util.List;

/**
 * 登录日志 Service
 */
public interface LogLoginService extends IService<SysLogLogin> {

    PageResult<SysLogLogin> pageList(LogLoginQueryDTO query);

    PageResult<SysLogLogin> pageListByUsername(String username, int current, int size);

    void deleteByIds(List<Long> ids);

    void clear();
}
