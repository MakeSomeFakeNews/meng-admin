package me.dqq.admin.module.role.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增/编辑角色参数
 */
@Data
public class RoleSaveDTO {

    private Long id;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    private Integer sort = 0;

    private Integer status = 1;

    private String remark;
}
