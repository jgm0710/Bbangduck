package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.dto.controller.response.*;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewSurvey;

import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.isNotNull;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * Review 응답 시 여러 Controller 에서 동시에 사용될 수 있는 부분에 대해
 * 코드 재사용율을 높이기 위해 구현한 utility class
 */
public class ReviewResponseUtils {
    public static ReviewResponseDto convertReviewToResponseDto(Review review, Member currentMember, boolean existsReviewLike, boolean possibleOfAddReviewSurvey) {
        ReviewSurvey reviewSurvey = review.getReviewSurvey();

        switch (review.getReviewType()) {
            case BASE:
                return isNotNull(reviewSurvey) ?
                        SimpleAndSurveyReviewResponseDto.convert(review, currentMember, existsReviewLike, possibleOfAddReviewSurvey) :
                        SimpleReviewResponseDto.convert(review, currentMember, existsReviewLike, possibleOfAddReviewSurvey);
            case DETAIL:
                return isNotNull(reviewSurvey) ?
                        DetailAndSurveyReviewResponseDto.convert(review, currentMember, existsReviewLike, possibleOfAddReviewSurvey) :
                        DetailReviewResponseDto.convert(review, currentMember, existsReviewLike, possibleOfAddReviewSurvey);
            default:
                return null;
        }
    }
}
