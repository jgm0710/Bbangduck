package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ConflictException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 가입, 회원 프로필 수정 등에서 기존 회원과 Email, Nickname, Social 정보 등이 중복되는
 * 회원이 있을 경우 발생할 예외
 */
public class MemberDuplicateException extends ConflictException {
    public MemberDuplicateException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public MemberDuplicateException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
