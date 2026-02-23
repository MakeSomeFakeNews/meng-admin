package me.dqq.admin.module.menu.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树响应 VO
 */
@Data
public class MenuTreeVO {

    private Long id;
    private Long parentId;
    private String title;
    private String name;
    private String path;
    private String component;
    private String icon;
    private Integer menuType;
    private String permission;
    private Integer sort;
    private Integer visible;
    private Integer status;

    /** 子菜单 */
    private List<MenuTreeVO> children = new ArrayList<>();
}
