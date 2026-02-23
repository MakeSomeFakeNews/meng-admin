package me.dqq.admin.module.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.dqq.admin.common.result.PageResult;
import me.dqq.admin.module.log.dto.LogOperateQueryDTO;
import me.dqq.admin.module.log.entity.SysLogOperate;

import java.util.List;

/**
 * 操作日志 Service
 */
public interface LogOperateService extends IService<SysLogOperate> {

    PageResult<SysLogOperate> pageList(LogOperateQueryDTO query);

    void deleteByIds(List<Long> ids);

    void clear();
}
