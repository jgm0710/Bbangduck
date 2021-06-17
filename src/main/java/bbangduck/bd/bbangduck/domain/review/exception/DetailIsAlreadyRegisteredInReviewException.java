package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : 정구민 <br>
 * 작성 일자 : 2021-06-14 <br><br>
 *
 * 리뷰에 리뷰 상세 등록 시 이미 리뷰 상세가 등록된 리뷰일 경우 발생할 예외
 */
public class DetailIsAlreadyRegisteredInReviewException extends BadRequestException {
    public DetailIsAlreadyRegisteredInReviewException() {
        super(ResponseStatus.DETAIL_IS_ALREADY_REGISTERED_IN_REVIEW);
    }
}
