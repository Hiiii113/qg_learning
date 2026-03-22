package hiiii113.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil
{
    // 加密
    public static String encode(String rawPassword)
    {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    // 校验
    public static boolean matches(String rawPassword, String encodedPassword)
    {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}