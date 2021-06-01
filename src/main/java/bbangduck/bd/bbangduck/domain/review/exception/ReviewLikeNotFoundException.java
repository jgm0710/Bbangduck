package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;
import bbangduck.bd.bbangduck.global.common.exception.NotFoundException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰에 등록된 좋아요 삭제 등에서 리뷰에 등록된 좋아요를 조회했을 때,
 * 해당 리뷰에 좋아요를 등록하지 않았을 경우 발생할 Exception
 */
public class ReviewLikeNotFoundException extends NotFoundException {
    public ReviewLikeNotFoundException(Long memberId, Long reviewId) {
        super(ResponseStatus.REVIEW_LIKE_NOT_FOUND, ResponseStatus.REVIEW_LIKE_NOT_FOUND.getMessage() + " MemberId : " + memberId + ", ReviewId : " + reviewId);
    }
}
