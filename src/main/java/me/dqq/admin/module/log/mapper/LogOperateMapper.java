package me.dqq.admin.module.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.dqq.admin.module.log.entity.SysLogOperate;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper
 */
@Mapper
public interface LogOperateMapper extends BaseMapper<SysLogOperate> {
}
