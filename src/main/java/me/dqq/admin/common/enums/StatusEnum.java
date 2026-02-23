package me.dqq.admin.common.enums;

import lombok.Getter;

/**
 * 状态枚举
 */
@Getter
public enum StatusEnum {

    /** 禁用 */
    DISABLED(0, "禁用"),
    /** 正常 */
    ENABLED(1, "正常");

    private final Integer status;
    private final String desc;

    StatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
