package me.dqq.admin.module.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.dqq.admin.module.log.entity.SysLogLogin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志 Mapper
 */
@Mapper
public interface LogLoginMapper extends BaseMapper<SysLogLogin> {
}
