package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ForbiddenException;

/**
 * 작성자 : 정구민 <br>
 * 작성 일자 : 2021-06-13 <br><br>
 *
 * 다른 회원이 생성한 리뷰를 조작하는 경우 발생할 예외
 */
public class ReviewCreatedByOtherMembersException extends ForbiddenException {
    public ReviewCreatedByOtherMembersException(ResponseStatus responseStatus) {
        super(responseStatus);
    }
}
