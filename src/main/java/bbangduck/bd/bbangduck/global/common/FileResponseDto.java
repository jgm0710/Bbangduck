package bbangduck.bd.bbangduck.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponseDto {

    private String fileDownloadUrl;

    private String fileThumbnailDownloadUrl;

}
