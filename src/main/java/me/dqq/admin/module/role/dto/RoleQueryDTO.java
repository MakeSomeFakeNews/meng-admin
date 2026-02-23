package me.dqq.admin.module.role.dto;

import lombok.Data;

/**
 * 角色查询参数
 */
@Data
public class RoleQueryDTO {

    private String roleName;
    private String roleCode;
    private Integer status;
    private Long current = 1L;
    private Long size = 10L;
}
