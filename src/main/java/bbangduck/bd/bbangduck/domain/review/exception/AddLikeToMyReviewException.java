package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰에 좋아요 등록 시 해당 리뷰가 자신이 생성한 리뷰일 경우 발생할 Exception
 */
public class AddLikeToMyReviewException extends BadRequestException {
    public AddLikeToMyReviewException() {
        super(ResponseStatus.ADD_LIKE_TO_MY_REVIEW);
    }
}
