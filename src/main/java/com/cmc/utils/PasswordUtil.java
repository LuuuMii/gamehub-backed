package com.cmc.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    public static String hashPassword(String plainPassword) {
        // 生成带盐的哈希
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12)); // 12 是工作因子
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        // 校验密码是否匹配
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
