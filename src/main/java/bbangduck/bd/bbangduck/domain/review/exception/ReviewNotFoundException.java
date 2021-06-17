package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.NotFoundException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 조회 시 리뷰를 찾을 수 없는 경우 발생할 예외
 */
public class ReviewNotFoundException extends NotFoundException {

    public ReviewNotFoundException() {
        super(ResponseStatus.REVIEW_NOT_FOUND);
    }
}
