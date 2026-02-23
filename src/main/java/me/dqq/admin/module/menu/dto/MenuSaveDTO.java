package me.dqq.admin.module.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜单保存参数
 */
@Data
public class MenuSaveDTO {

    private Long id;

    @NotNull(message = "父菜单ID不能为空")
    private Long parentId;

    @NotBlank(message = "菜单标题不能为空")
    private String title;

    private String name;
    private String path;
    private String component;
    private String icon;

    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    private String permission;

    private Integer sort = 0;

    private Integer visible = 1;

    private Integer status = 1;
}
