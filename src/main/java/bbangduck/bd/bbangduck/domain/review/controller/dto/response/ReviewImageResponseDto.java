package bbangduck.bd.bbangduck.domain.review.controller.dto.response;

import bbangduck.bd.bbangduck.domain.review.entity.ReviewImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static bbangduck.bd.bbangduck.global.common.LinkToUtils.linkToFileDownload;
import static bbangduck.bd.bbangduck.global.common.LinkToUtils.linkToImageFileThumbnailDownload;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 조회 시 리뷰에 등록된 이미지에 대한 응답 Data 를 담을 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewImageResponseDto {

    private Long reviewImageId;

    private String reviewImageUrl;

    private String reviewImageThumbnailUrl;

    public static ReviewImageResponseDto convert(ReviewImage reviewImage) {
        String fileName = reviewImage.getFileName();
        String reviewImageUrl = linkToFileDownload(fileName);
        String reviewImageThumbnailUrl = linkToImageFileThumbnailDownload(fileName);

        return ReviewImageResponseDto.builder()
                .reviewImageId(reviewImage.getId())
                .reviewImageUrl(reviewImageUrl)
                .reviewImageThumbnailUrl(reviewImageThumbnailUrl)
                .build();
    }
}
