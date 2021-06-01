package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ConflictException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰에 좋아요 등록 시 해당 리뷰에 이미 좋아요를 등록했을 경우 발생할 Exception
 */
public class ExistsReviewLikeException extends ConflictException {
    public ExistsReviewLikeException(Long memberId, Long reviewId) {
        super(ResponseStatus.EXISTS_REVIEW_LIKE, ResponseStatus.EXISTS_REVIEW_LIKE.getMessage() + " MemberId : " + memberId + ", ReviewId : " + reviewId);
    }
}
