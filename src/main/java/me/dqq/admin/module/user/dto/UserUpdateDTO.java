package me.dqq.admin.module.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 编辑用户参数
 */
@Data
public class UserUpdateDTO {

    /** 用户ID */
    @NotNull(message = "用户ID不能为空")
    private Long id;

    /** 昵称 */
    private String nickname;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 状态 */
    private Integer status;
}
