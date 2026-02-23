package me.dqq.admin.module.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志实体
 */
@Data
@TableName("sys_log_login")
public class SysLogLogin {

    /** 主键ID（雪花算法） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 用户名 */
    private String username;

    /** 登录IP */
    private String ip;

    /** IP归属地 */
    private String ipLocation;

    /** 浏览器 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 登录状态（1成功/0失败） */
    private Integer status;

    /** 提示信息 */
    private String message;

    /** 登录时间 */
    private LocalDateTime loginTime;
}
