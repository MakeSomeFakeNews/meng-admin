package me.dqq.admin.module.menu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜单/权限实体
 */
@Data
@TableName("sys_menu")
public class SysMenu {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 父菜单ID（0为顶级） */
    private Long parentId;

    /** 菜单标题 */
    private String title;

    /** 路由name */
    private String name;

    /** 路由路径 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 图标 */
    private String icon;

    /** 菜单类型（0目录/1菜单/2按钮） */
    private Integer menuType;

    /** 权限标识（如sys:user:list） */
    private String permission;

    /** 排序 */
    private Integer sort;

    /** 是否可见（1是/0否） */
    private Integer visible;

    /** 状态（1正常/0禁用） */
    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
