package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ConflictException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰에 좋아요 등록 시 해당 리뷰에 이미 좋아요를 등록했을 경우 발생할 Exception
 */
public class ReviewHasAlreadyBeenLikedException extends ConflictException {
    public ReviewHasAlreadyBeenLikedException(Long memberId, Long reviewId) {
        super(ResponseStatus.REVIEW_HAS_ALREADY_BEEN_LIKED, ResponseStatus.REVIEW_HAS_ALREADY_BEEN_LIKED.getMessage() + " MemberId : " + memberId + ", ReviewId : " + reviewId);
    }
}
