package bbangduck.bd.bbangduck.global.common;

import bbangduck.bd.bbangduck.domain.file.controller.FileStorageApiController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * 작성자 : 정구민 <br><br>
 *
 * WebMvcLinkBuilder.linkTo 를 사용하여 특정 링크를 응답으로 보낼 경우
 * 중복을 줄이기 위해 구현한 Util Class
 */
public class LinkToUtils {

    public static String  linkToFileDownload(String fileName) {
        if (fileNameNotExists(fileName)) {
            return null;
        }
        String encodedFileName = getEncodedFileName(fileName);
        return linkTo(FileStorageApiController.class).slash(encodedFileName).toUri().toString();
    }

    private static boolean fileNameNotExists(String fileName) {
        return fileName == null || fileName.isBlank();
    }

    public static String linkToImageFileThumbnailDownload(String fileName) {
        if (fileNameNotExists(fileName)) {
            return null;
        }
        String encodedFileName = getEncodedFileName(fileName);
        return linkTo(FileStorageApiController.class).slash("images").slash("thumbnails").slash(encodedFileName).toUri().toString();
    }

    private static String getEncodedFileName(String fileName) {
        return URLEncoder.encode(fileName, StandardCharsets.UTF_8);
    }
}
