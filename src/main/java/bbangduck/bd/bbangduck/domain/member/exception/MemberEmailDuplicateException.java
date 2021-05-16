package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ConflictException;
import org.springframework.boot.context.config.ConfigDataException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원가입, 회원정보 수정 등에서 새로 저장할 회원의 Email 이
 * 이미 기존에 존재하는 회원의 Email 과 중복될 경우 발생할 예외
 */
public class MemberEmailDuplicateException extends ConflictException {
    public MemberEmailDuplicateException(String email) {
        super(ResponseStatus.MEMBER_EMAIL_DUPLICATE, ResponseStatus.MEMBER_EMAIL_DUPLICATE.getMessage() + " Email : " + email);
    }
}
