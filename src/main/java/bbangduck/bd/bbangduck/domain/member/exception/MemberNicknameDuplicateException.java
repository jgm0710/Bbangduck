package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ConflictException;

public class MemberNicknameDuplicateException extends ConflictException {
    public MemberNicknameDuplicateException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public MemberNicknameDuplicateException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
