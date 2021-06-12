package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ForbiddenException;

/**
 * 작성자 : JGM <br>
 * 작성 일자 : 2021-06-12 <br><br>
 *
 * 다른 회원이 생성한 리뷰를 수정하는 경우 발생할 예외
 */
public class UpdateReviewCreatedByOtherMembersException extends ForbiddenException {
    public UpdateReviewCreatedByOtherMembersException() {
        super(ResponseStatus.UPDATE_REVIEW_CREATED_BY_OTHER_MEMBERS);
    }
}
