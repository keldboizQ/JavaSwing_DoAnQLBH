package util;

import java.security.MessageDigest;

public class PasswordUtil {

    public static byte[] hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((password + salt).getBytes("UTF-16LE"));
            return md.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
