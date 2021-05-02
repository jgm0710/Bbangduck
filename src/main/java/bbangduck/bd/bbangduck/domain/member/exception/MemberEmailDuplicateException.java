package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ConflictException;

public class MemberEmailDuplicateException extends ConflictException {
    public MemberEmailDuplicateException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public MemberEmailDuplicateException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
