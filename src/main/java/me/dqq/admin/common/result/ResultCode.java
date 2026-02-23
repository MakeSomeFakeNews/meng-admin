package me.dqq.admin.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    UNAUTHORIZED(401, "未认证，请先登录"),
    FORBIDDEN(403, "权限不足，拒绝访问"),
    NOT_FOUND(404, "资源不存在"),
    VALIDATE_FAILED(422, "参数校验失败"),
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已禁用"),
    USERNAME_OR_PASSWORD_ERROR(1003, "用户名或密码错误"),
    OLD_PASSWORD_ERROR(1004, "旧密码不正确"),
    USERNAME_EXISTS(1005, "用户名已存在"),
    ROLE_CODE_EXISTS(1006, "角色编码已存在");

    /** 状态码 */
    private final Integer code;
    /** 提示信息 */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
