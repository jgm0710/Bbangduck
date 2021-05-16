package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ConflictException;
import org.springframework.boot.context.config.ConfigDataException;

// TODO: 2021-05-16 주석
public class MemberEmailDuplicateException extends ConflictException {
    public MemberEmailDuplicateException(String email) {
        super(ResponseStatus.MEMBER_EMAIL_DUPLICATE, ResponseStatus.MEMBER_EMAIL_DUPLICATE.getMessage() + " Email : " + email);
    }
}
