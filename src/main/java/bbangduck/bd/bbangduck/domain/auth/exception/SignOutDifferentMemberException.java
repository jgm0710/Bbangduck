package bbangduck.bd.bbangduck.domain.auth.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ForbiddenException;


/**
 * 작성자 : 정구민 <br><br>
 *
 * 로그아웃 요청 시 다른 회원을 로그아웃 하는 경우 발생할 예외
 */
public class SignOutDifferentMemberException extends ForbiddenException {
    public SignOutDifferentMemberException() {
        super(ResponseStatus.SIGN_OUT_DIFFERENT_MEMBER);
    }
}
