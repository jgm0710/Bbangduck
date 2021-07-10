package bbangduck.bd.bbangduck.domain.review.dto.service;

import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * 리뷰의 기본 정보 수정 Data 를 담을 Dto
 *
 * @author Gumin Jeong
 * @since 2021-07-11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewUpdateBaseDto {

    private ReviewType reviewType;

    private boolean clearYN;

    private LocalTime clearTime;

    private ReviewHintUsageCount hintUsageCount;

    private Integer rating;

}
