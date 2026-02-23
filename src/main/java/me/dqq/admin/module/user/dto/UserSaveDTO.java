package me.dqq.admin.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 新增用户参数
 */
@Data
public class UserSaveDTO {

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 32, message = "用户名长度为3-32个字符")
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 64, message = "密码长度至少8位")
    private String password;

    /** 昵称 */
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 状态（1正常/0禁用） */
    private Integer status = 1;

    /** 初始角色ID列表 */
    private List<Long> roleIds;
}
