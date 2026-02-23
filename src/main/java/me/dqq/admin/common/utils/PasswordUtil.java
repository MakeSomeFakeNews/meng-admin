package me.dqq.admin.common.utils;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码工具类（BCrypt 加密）
 */
public class PasswordUtil {

    /**
     * BCrypt 加密
     */
    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * 验证密码
     */
    public static boolean verify(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
