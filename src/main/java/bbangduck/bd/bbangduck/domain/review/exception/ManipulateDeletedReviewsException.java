package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : JGM <br>
 * 작성 일자 : 2021-06-12 <br><br>
 *
 * 삭제된 리뷰를 사용하여 데이터 조작을 하는 경우 발생할 예외
 */
public class ManipulateDeletedReviewsException extends BadRequestException {
    public ManipulateDeletedReviewsException() {
        super(ResponseStatus.MANIPULATE_DELETED_REVIEW);
    }
}
