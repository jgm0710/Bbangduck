package bbangduck.bd.bbangduck.domain.review.controller.dto.response;

import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static bbangduck.bd.bbangduck.global.common.LinkToUtils.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 조회 시 리뷰가 등록된 테마에 대한 간단한 정보들을 응답하기 위해 구현,
 * Theme 에 대한 간단한 응답 Data 를 담을 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewThemeSimpleInfoResponseDto {

    private Long themeId;

    private String themeName;

    private String themeImageUrl;

    private String themeImageThumbnailUrl;

    public static ReviewThemeSimpleInfoResponseDto convert(Theme theme) {
        if (theme == null) {
            return null;
        }

        String fileName = theme.getThemeImageFileName();
        String themeImageUrl = linkToFileDownload(fileName);
        String themeImageThumbnailUrl = linkToImageFileThumbnailDownload(fileName);

        return ReviewThemeSimpleInfoResponseDto.builder()
                .themeId(theme.getId())
                .themeName(theme.getName())
                .themeImageUrl(themeImageUrl)
                .themeImageThumbnailUrl(themeImageThumbnailUrl)
                .build();
    }
}
