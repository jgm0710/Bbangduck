package bbangduck.bd.bbangduck.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponseDto {

    private Long fileId;

    private String fileName;

    private String fileStoragePath;

    private String fileDownloadUrl;

    private String fileThumbnailDownloadUrl;

    private String fileType;

    private String fileSize;

//.fileId()
//.fileName()
//.fileStoragePath()
//.fileDownloadUrl()
//.fileThumbnailDownloadUrl()
//.fileType()
//.fileSize()
}
