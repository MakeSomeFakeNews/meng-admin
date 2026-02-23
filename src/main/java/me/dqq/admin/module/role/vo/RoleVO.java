package me.dqq.admin.module.role.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色响应 VO
 */
@Data
public class RoleVO {

    private Long id;
    private String roleName;
    private String roleCode;
    private Integer sort;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
}
