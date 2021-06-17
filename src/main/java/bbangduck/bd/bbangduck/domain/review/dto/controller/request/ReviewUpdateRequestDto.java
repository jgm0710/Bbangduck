package bbangduck.bd.bbangduck.domain.review.dto.controller.request;

import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewImageDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.existsList;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 수정 요청에 필요한 요청 Body 의 Data 를 담을 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewUpdateRequestDto {

    /**
     * 간단 리뷰 요청 body
     */

    @NotNull(message = "리뷰 타입을 기입해 주세요.")
    private ReviewType reviewType;

    @NotNull(message = "게임 클리어 여부를 기입해주세요.")
    private Boolean clearYN;

    private LocalTime clearTime;

    @NotNull(message = "사용한 힌트 개수를 기입해 주세요.")
    private ReviewHintUsageCount hintUsageCount;

    @NotNull(message = "테마에 대한 평점을 기입해 주세요.")
    @Range(min = 1, max = 5, message = "테마에 대한 평점은 1~5점만 기입이 가능합니다.")
    private Integer rating;

    private List<Long> friendIds;

    /**
     * 상세 리뷰 요청 body
     */

    private List<ReviewImageRequestDto> reviewImages;

    @Length(max = 2000, message = "코멘트는 2000자를 넘길 수 없습니다.")
    private String comment;


    public ReviewUpdateDto toServiceDto() {

        List<ReviewImageDto> reviewImageDtos = convertReviewImagesToServiceDto();

        return ReviewUpdateDto.builder()
                .reviewType(reviewType)
                .clearYN(clearYN)
                .clearTime(clearTime)
                .hintUsageCount(hintUsageCount)
                .rating(rating)
                .friendIds(friendIds)
                .reviewImages(reviewImageDtos)
                .comment(comment)
                .build();
    }

    private List<ReviewImageDto> convertReviewImagesToServiceDto() {
        if (existsList(reviewImages)) {
            return reviewImages.stream().map(ReviewImageRequestDto::toServiceDto).collect(Collectors.toList());
        }
        return null;
    }
}
