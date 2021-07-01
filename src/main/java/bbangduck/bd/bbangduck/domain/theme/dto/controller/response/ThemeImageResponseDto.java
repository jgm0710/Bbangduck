package bbangduck.bd.bbangduck.domain.theme.dto.controller.response;

import bbangduck.bd.bbangduck.domain.theme.entity.ThemeImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static bbangduck.bd.bbangduck.global.common.LinkToUtils.linkToFileDownload;
import static bbangduck.bd.bbangduck.global.common.LinkToUtils.linkToImageFileThumbnailDownload;

/**
 * 테마 관련 정보를 응답 할 때, 테마 이미지 관련 정보들을 응답 Body Data 에 싣기 위해 변환해주는 Dto
 *
 * @author jgm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeImageResponseDto {

    private Long themeImageId;

    private String themeImageUrl;

    private String themeImageThumbnailUrl;

    public static ThemeImageResponseDto convert(ThemeImage themeImage) {
        String themeImageUrl = linkToFileDownload(themeImage.getFileName());
        String themeImageThumbnailUrl = linkToImageFileThumbnailDownload(themeImage.getFileName());

        return ThemeImageResponseDto.builder()
                .themeImageId(themeImage.getId())
                .themeImageUrl(themeImageUrl)
                .themeImageThumbnailUrl(themeImageThumbnailUrl)
                .build();
    }

}
