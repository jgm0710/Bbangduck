package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ForbiddenException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰에 설문 추가 시 다른 회원이 생성한 리뷰에 설문을 추가하는 경우 발생할 예외
 */
public class AddSurveysToReviewsCreatedByOtherMembersException extends ForbiddenException {
    public AddSurveysToReviewsCreatedByOtherMembersException() {
        super(ResponseStatus.ADD_SURVEYS_TO_REVIEWS_CREATED_BY_OTHER_MEMBERS);
    }
}
