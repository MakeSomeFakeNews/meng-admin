package me.dqq.admin.common.enums;

import lombok.Getter;

/**
 * 菜单类型枚举
 */
@Getter
public enum MenuTypeEnum {

    /** 目录 */
    DIRECTORY(0, "目录"),
    /** 菜单 */
    MENU(1, "菜单"),
    /** 按钮 */
    BUTTON(2, "按钮");

    private final Integer type;
    private final String desc;

    MenuTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
