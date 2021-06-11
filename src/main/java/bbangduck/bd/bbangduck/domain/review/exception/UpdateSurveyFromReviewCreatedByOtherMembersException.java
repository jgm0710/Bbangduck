package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ForbiddenException;

/**
 * 작성자 : 정구민 <br>
 * 작성 일자 : 2021-06-11 <br><br>
 *
 * 리뷰에 등록된 설문 수정 시 다른 회원이 생성한 리뷰의 설문을 수정하는 경우 발생할 예외
 */
public class UpdateSurveyFromReviewCreatedByOtherMembersException extends ForbiddenException {
    public UpdateSurveyFromReviewCreatedByOtherMembersException() {
        super(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_CREATED_BY_OTHER_MEMBERS);
    }
}
