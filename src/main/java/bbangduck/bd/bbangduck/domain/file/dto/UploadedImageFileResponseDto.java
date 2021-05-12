package bbangduck.bd.bbangduck.domain.file.dto;

import bbangduck.bd.bbangduck.domain.file.controller.FileStorageApiController;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadedImageFileResponseDto {

    private Long fileId;

    private String fileName;

    private String fileDownloadUrl;

    private String fileThumbnailDownloadUrl;

    public static UploadedImageFileResponseDto convert(FileStorage fileStorage) throws UnsupportedEncodingException {
        String fileName = fileStorage.getFileName();
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        String downloadUrl = linkTo(FileStorageApiController.class).slash(encodedFileName).toUri().toString();
        String thumbnailDownloadUrl = linkTo(FileStorageApiController.class).slash("images").slash("thumbnails").slash(encodedFileName).toUri().toString();

        return UploadedImageFileResponseDto.builder()
                .fileId(fileStorage.getId())
                .fileName(fileStorage.getFileName())
                .fileDownloadUrl(downloadUrl)
                .fileThumbnailDownloadUrl(thumbnailDownloadUrl)
                .build();
    }
}
