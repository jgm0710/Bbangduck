package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 *  * 작성자 : 정구민 <br>
 *  * 작성 일자 : 2021-06-11 <br><br>
 *
 *  리뷰 설문 수정 시 리뷰에 설문이 등록되어 있지 않을 경우 발생할 예외
 */
public class ReviewHasNotSurveyException extends BadRequestException {
    public ReviewHasNotSurveyException() {
        super(ResponseStatus.REVIEW_HAS_NOT_SURVEY);
    }
}
