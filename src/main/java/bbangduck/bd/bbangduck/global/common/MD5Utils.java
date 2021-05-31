package bbangduck.bd.bbangduck.global.common;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 파일 다운로드 등에서 캐시된 파일의 수정 검증을 위해 사용하는
 * Etag 를 위해 구현한 Class
 */
public class MD5Utils {
    public static String encode(String input) {
        byte[] bytesInput = input.getBytes(StandardCharsets.UTF_8);
        return DigestUtils.md5DigestAsHex(bytesInput);
    }
}
