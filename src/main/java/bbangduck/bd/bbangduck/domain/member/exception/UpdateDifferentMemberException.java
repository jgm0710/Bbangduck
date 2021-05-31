package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ForbiddenException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 다른 회원의 프로필을 수정할 경우 발생할 예외
 */
public class UpdateDifferentMemberException extends ForbiddenException {
    public UpdateDifferentMemberException() {
        super(ResponseStatus.UPDATE_DIFFERENT_MEMBER_PROFILE);
    }
}
