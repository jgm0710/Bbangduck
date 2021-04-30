package bbangduck.bd.bbangduck.global.common.Exception;

import bbangduck.bd.bbangduck.domain.member.controller.status.MemberResponseStatus;

public class NotFoundException extends StatusException {
    public NotFoundException(MemberResponseStatus memberResponseStatus) {
        super(memberResponseStatus);
    }

    public NotFoundException(MemberResponseStatus memberResponseStatus, String message) {
        super(memberResponseStatus, message);
    }
}
