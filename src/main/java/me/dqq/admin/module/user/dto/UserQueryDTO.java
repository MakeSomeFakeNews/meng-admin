package me.dqq.admin.module.user.dto;

import lombok.Data;

/**
 * 用户分页查询参数
 */
@Data
public class UserQueryDTO {

    /** 用户名（模糊） */
    private String username;

    /** 昵称（模糊） */
    private String nickname;

    /** 状态 */
    private Integer status;

    /** 当前页（默认1） */
    private Long current = 1L;

    /** 每页条数（默认10） */
    private Long size = 10L;
}
