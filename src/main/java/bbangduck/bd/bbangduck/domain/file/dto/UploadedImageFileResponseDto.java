package bbangduck.bd.bbangduck.domain.file.dto;

import bbangduck.bd.bbangduck.domain.file.controller.FileStorageApiController;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadedImageFileResponseDto {

    private Long fileId;

    private String fileName;

    private String uploadPath;

    public static UploadedImageFileResponseDto convert(FileStorage fileStorage) {
        return UploadedImageFileResponseDto.builder()
                .fileId(fileStorage.getId())
                .fileName(fileStorage.getFileName())
                .uploadPath(fileStorage.getUploadPathString())
                .build();
    }
}
