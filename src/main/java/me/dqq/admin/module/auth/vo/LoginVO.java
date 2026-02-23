package me.dqq.admin.module.auth.vo;

import lombok.Data;
import me.dqq.admin.module.menu.vo.MenuTreeVO;

import java.util.List;

/**
 * 登录响应 VO
 */
@Data
public class LoginVO {

    /** Token 名称（Authorization） */
    private String tokenName;

    /** Token 值 */
    private String tokenValue;

    /** 用户基本信息 */
    private UserInfoVO userInfo;

    /** 权限标识列表（如 sys:user:list） */
    private List<String> permissions;

    /** 角色编码列表 */
    private List<String> roles;

    /** 菜单树（用于前端动态路由生成） */
    private List<MenuTreeVO> menus;

    /**
     * 用户基本信息内嵌VO
     */
    @Data
    public static class UserInfoVO {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
    }
}
