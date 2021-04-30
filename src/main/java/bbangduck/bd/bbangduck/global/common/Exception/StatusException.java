package bbangduck.bd.bbangduck.global.common.Exception;

import bbangduck.bd.bbangduck.domain.member.controller.status.MemberResponseStatus;

public class StatusException extends RuntimeException{

    private final int status;

    public StatusException(MemberResponseStatus memberResponseStatus) {
        super(memberResponseStatus.getMessage());
        this.status = memberResponseStatus.getStatus();
    }

    public StatusException(MemberResponseStatus memberResponseStatus, String message) {
        super(message);
        this.status = memberResponseStatus.getStatus();
    }
}
