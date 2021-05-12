package bbangduck.bd.bbangduck.global.common;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public class MD5Utils {
    public static String encode(String input) {
        byte[] bytesInput = input.getBytes(StandardCharsets.UTF_8);
        return DigestUtils.md5DigestAsHex(bytesInput);
    }
}
