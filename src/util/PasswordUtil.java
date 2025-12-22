package util;

import java.nio.charset.Charset;
import java.security.MessageDigest;

public class PasswordUtil {

    public static byte[] hash(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String input = password + salt;

            // ⚠️ Quan trọng: dùng UTF-16LE để khớp NVARCHAR của SQL Server
            return md.digest(input.getBytes(Charset.forName("UTF-16LE")));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}