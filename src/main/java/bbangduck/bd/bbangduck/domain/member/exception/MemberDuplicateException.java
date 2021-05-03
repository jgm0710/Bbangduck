package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ConflictException;

public class MemberDuplicateException extends ConflictException {
    public MemberDuplicateException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public MemberDuplicateException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
