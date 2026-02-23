package me.dqq.admin.module.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("sys_log_operate")
public class SysLogOperate {

    /** 主键ID（雪花算法） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 模块名 */
    private String module;

    /** 操作名 */
    private String action;

    /** 方法名（全限定名.方法名） */
    private String method;

    /** 请求URL */
    private String requestUrl;

    /** 请求方式（GET/POST/PUT/DELETE） */
    private String requestMethod;

    /** 请求参数（JSON格式，密码字段已脱敏） */
    private String requestParams;

    /** 响应结果（JSON格式，最多2000字符） */
    private String responseResult;

    /** 操作状态（1正常/0异常） */
    private Integer status;

    /** 异常信息 */
    private String errorMsg;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人用户名 */
    private String operatorName;

    /** 客户端IP */
    private String ip;

    /** IP归属地 */
    private String ipLocation;

    /** 执行耗时（毫秒） */
    private Long duration;

    /** 操作时间 */
    private LocalDateTime createTime;
}
